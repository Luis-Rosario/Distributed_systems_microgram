package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import discovery.Discovery;
import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import kakfa.KafkaUtils;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.clt.java.ClientFactory;
import microgram.impl.clt.rest.RestProfilesClient;
import microgram.impl.srv.rest.ProfilesRestServer;
import microgram.impl.srv.rest.RestResource;

public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {
	public static String SERVICE = "Microgram-Profiles";

	protected Map<String, Profile> users = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> followers = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> following = new ConcurrentHashMap<>();

	protected Profiles profilesClient = null;

	public static final String PROFILES_EVENTS = "Microgram-ProfilesEvents";

	private int myN = 0;
	private URI[] aux;

	enum ProfilesEventKeys {
		DELETEPROFILE, SUCCESS
	};

	final KafkaPublisher kafka;

	public JavaProfiles() {
		this.kafka = new KafkaPublisher();
		KafkaUtils.createTopics(Arrays.asList(JavaProfiles.PROFILES_EVENTS));
		new Thread(() -> {
			listen();
		}).start();
	}

	public JavaProfiles(int n, String server_uri) {
		this.kafka = new KafkaPublisher();
		KafkaUtils.createTopics(Arrays.asList(JavaProfiles.PROFILES_EVENTS));
		new Thread(() -> {
			listen();
		}).start();

		if (n > 1) {
			try {
				long start = System.currentTimeMillis();
				// System.err.println(" n = " + n);
				for (;;) {
					aux = Discovery.findUrisOf(ProfilesRestServer.SERVICE, n);
					// System.err.println("DISCOVERY AGAIN -> " + aux);

					if (System.currentTimeMillis() - start < 20000) {
						if (aux != null) {
							if (aux.length == n)
								break;
						}
					} else {
						throw new IOException();
					}

				}
				// System.err.println("OUT OF CYCLE");
				Arrays.sort(aux);
				for (int i = 0; i < aux.length; i++) {
					if (aux[i].toString().equals(server_uri)) // verificar se o equals funciona
						myN = i;

				}
				System.err.println("Crise ident?: " + myN);
				for (URI a : aux)
					System.err.println(" URI -> " + a.toString());

			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		int pos = resourceServerLocation(userId);
		// System.err.println("POS = " + pos + " //// myN -> " + myN + " ///////////////
		// NAME-> " + userId);
		if (pos != myN) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).getProfile(userId);

		} else {
			Profile res = users.get(userId);
			if (res == null)
				return error(NOT_FOUND);

			res.setFollowers(followers.get(userId).size());
			res.setFollowing(following.get(userId).size());
			return ok(res);
		}
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		int pos = resourceServerLocation(profile.getUserId());
		// System.err.println( "POS = " + pos + " //// myN -> " + myN + "
		// /////////////// NAME-> " + profile.getUserId());
		if (pos == myN) {
			// System.err.println("mandei in");
			Profile res = users.putIfAbsent(profile.getUserId(), profile);

			// System.err.println(resourceServerLocation(profile.getUserId()) + " DENTRO DE
			// MIM ");
			if (res != null) {
				// System.err.println("asdas");
				return error(CONFLICT);
			}

			followers.put(profile.getUserId(), ConcurrentHashMap.newKeySet());
			following.put(profile.getUserId(), ConcurrentHashMap.newKeySet());
			kafka.publish(PROFILES_EVENTS, ProfilesEventKeys.SUCCESS.name(), profile.getPhotoUrl());
			return ok();
		} else {
			// System.err.println("mandei out");
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(profile.getUserId())])
					.createProfile(profile);
		}

	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		int pos = resourceServerLocation(userId);
		if (pos != myN && aux.length > 1) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).deleteProfile(userId);
		} else {
			Profile profileToDelete = users.get(userId);

			if (profileToDelete != null) {
				users.remove(userId);
				Set<String> profileFollows = following.remove(userId);
				Set<String> profileFollowers = followers.remove(userId);
				Profile res = null;

				for (String a : profileFollows) {
					followers.get(a).remove(userId);
					res = users.get(a);
					res.setFollowers(res.getFollowers() - 1);
				}

				for (String a : profileFollowers) {
					following.get(a).remove(userId);
					res = users.get(a);
					res.setFollowing(res.getFollowing() - 1);
				}
				kafka.publish(PROFILES_EVENTS, ProfilesEventKeys.DELETEPROFILE.name(), userId);
				return ok();
			}

			else {
				return error(NOT_FOUND);
			}
		}

	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return ok(users.values().stream().filter(p -> p.getUserId().startsWith(prefix)).collect(Collectors.toList()));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		if (aux.length == 1) {
			Set<String> s1 = following.get(userId1);
			Set<String> s2 = followers.get(userId2);
			Profile u1 = users.get(userId1);
			Profile u2 = users.get(userId2);

			if (s1 == null || s2 == null)
				return error(NOT_FOUND);

			if (isFollowing) {
				boolean added1 = s1.add(userId2), added2 = s2.add(userId1);
				if (!added1 || !added2)
					return error(CONFLICT);
				u1.setFollowing(u1.getFollowing() - 1);
				u2.setFollowers(u2.getFollowers() - 1);
			} else {
				boolean removed1 = s1.remove(userId2), removed2 = s2.remove(userId1);
				if (!removed1 || !removed2)
					return error(NOT_FOUND);
				u1.setFollowing(u1.getFollowing() + 1);
				u2.setFollowers(u2.getFollowers() + 1);
			}
			return ok();
		} else {
			Result<Profile> p1 = getProfile(userId1);
			Result<Profile> p2 = getProfile(userId2);

			if (!p1.isOK() || !p2.isOK())
				return error(NOT_FOUND);

			Profile u1 = p1.value();
			Profile u2 = p2.value();
			Set<String> s1 = getfollowing(u1.getUserId()).value();
			Set<String> s2 = getfollowers(u2.getUserId()).value();
			
			if (s1 != null && s2 != null)
			System.err.println("s1  = " + s1.size() + " s2 -> " + s2.size());

			if (isFollowing) {
				boolean added1 = s1.add(userId2), added2 = s2.add(userId1);
				if (!added1 || !added2)
					return error(CONFLICT);
				
				u1.setFollowing(u1.getFollowing() - 1);
				u2.setFollowers(u2.getFollowers() - 1);
				
				
				// set
				setfollowing(u1.getUserId(), s1);
				setfollowers(u2.getUserId(),s2);
			} else {
				boolean removed1 = s1.remove(userId2), removed2 = s2.remove(userId1);
				if (!removed1 || !removed2)
					return error(NOT_FOUND);
				u1.setFollowing(u1.getFollowing() + 1);
				u2.setFollowers(u2.getFollowers() + 1);
				// set
				setfollowing(u1.getUserId(), s1);
				setfollowers(u2.getUserId(),s2);
			}

			return ok();
		}
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		if (aux.length == 1) {
			Set<String> s1 = following.get(userId1);
			Set<String> s2 = followers.get(userId2);

			if (s1 == null || s2 == null)
				return error(NOT_FOUND);
			else
				return ok(s1.contains(userId2) && s2.contains(userId1));
		} else {
			Result<Profile> p1 = getProfile(userId1);
			Result<Profile> p2 = getProfile(userId2);

			if (!p1.isOK() || !p2.isOK())
				return error(NOT_FOUND);

			Profile u1 = p1.value();
			Profile u2 = p2.value();
			Set<String> s1 = getfollowing(u1.getUserId()).value();
			Set<String> s2 = getfollowers(u2.getUserId()).value();

			if (s1 == null || s2 == null)
				return error(NOT_FOUND);
			else
				return ok(s1.contains(userId2) && s2.contains(userId1));
		}
	}

	@Override
	public Result<Set<String>> getfollowing(String userId) {
		int pos = resourceServerLocation(userId);
		if (pos != myN && aux.length > 1) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).getfollowing(userId);
		} else {
			if (users.get(userId) != null) {
				return ok(following.get(userId));
			} else
				return error(NOT_FOUND);
		}
	}
	
	

	@Override
	public Result<Set<String>> getfollowers(String userId) {
		int pos = resourceServerLocation(userId);
		if (pos != myN && aux.length > 1) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).getfollowers(userId);
		} else {
			if (users.get(userId) != null) {
				return ok(followers.get(userId));
			} else
				return error(NOT_FOUND);
		}
	}

	private void listen() {
		List<String> topics = Arrays.asList(JavaPosts.POSTS_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);

		subscriber.consume((topic, key, value) -> {
			Profile p = null;
			switch (key) {
			case "DELETEPOSTUSER":
				p = getProfile(value).value();
				p.setPosts(p.getPosts() - 1);
				break;
			case "CREATEPOST":
				p = getProfile(value).value();
				p.setPosts(p.getPosts() + 1);
				break;
			}
		});
	}

	private int resourceServerLocation(String id) {
		// System.err.println("id: " + id +"->" + Math.abs(id.hashCode() % aux.length));
		return Math.abs(id.hashCode() % aux.length);

	}

	@Override
	public Result<Void> setfollowing(String userId,Set<String> following) {
		int pos = resourceServerLocation(userId);
		if (pos != myN && aux.length > 1) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).setfollowers(userId,following);
		} else {
			if (users.get(userId) != null) {
				this.following.replace(userId, following);
				return ok();
			} else
				return error(NOT_FOUND);
		}
	}

	@Override
	public Result<Void> setfollowers(String userId,Set<String> followers) {
		int pos = resourceServerLocation(userId);
		if (pos != myN && aux.length > 1) {
			return ClientFactory.getProfilesClient(aux[resourceServerLocation(userId)]).setfollowers(userId,followers);
		} else {
			if (users.get(userId) != null) {
				this.followers.replace(userId, followers);
				return ok();
			} else
				return error(NOT_FOUND);
		}
	}

}
