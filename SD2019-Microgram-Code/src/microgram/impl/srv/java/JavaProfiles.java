package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import kakfa.KafkaUtils;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.rest.RestResource;

public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {
	public static String SERVICE = "Microgram-Posts";

	protected Map<String, Profile> users = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> followers = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> following = new ConcurrentHashMap<>();

	protected Posts postsClient = null;

	
	public static final String PROFILES_EVENTS = "Microgram-ProfilesEvents";

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

	@Override
	public Result<Profile> getProfile(String userId) {
		Profile res = users.get( userId );
		if( res == null ) 
			return error(NOT_FOUND);

		res.setFollowers( followers.get(userId).size() );
		res.setFollowing( following.get(userId).size() );
		return ok(res);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		Profile res = users.putIfAbsent( profile.getUserId(), profile );
		if( res != null ) 
			return error(CONFLICT);

		followers.put( profile.getUserId(), new HashSet<>());
		following.put( profile.getUserId(), new HashSet<>());
		kafka.publish(PROFILES_EVENTS, ProfilesEventKeys.SUCCESS.name(), profile.getPhotoUrl());
		return ok();
	}


	@Override
    public Result<Void> deleteProfile(String userId) {
	        Profile profileToDelete = users.get(userId);
	 
	        if(profileToDelete != null) {
	            users.remove(userId);
	            Set<String> profileFollows = following.remove(userId);
	            Set<String> profileFollowers = followers.remove(userId);
	            Profile res =null;

	            for(String a:profileFollows) {
	                followers.get(a).remove(userId);
	                 res = users.get( a );
	                 res.setFollowers(res.getFollowers() - 1);
	            }

	            for(String a:profileFollowers) {
	                following.get(a).remove(userId);
	                 res = users.get( a );
	                 res.setFollowing(res.getFollowing() - 1);
	            }
	            kafka.publish(PROFILES_EVENTS, ProfilesEventKeys.DELETEPROFILE.name(), userId);
	            return ok();
	        }

	        else {
	            return error(NOT_FOUND);
	        }
	    }
    

	@Override
	public Result<List<Profile>> search(String prefix) {
		return ok(users.values().stream()
				.filter( p -> p.getUserId().startsWith( prefix ) )
				.collect( Collectors.toList()));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {		
		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );
		Profile u1 = users.get( userId1 );
		Profile u2 = users.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);

		if( isFollowing ) {
			boolean added1 = s1.add(userId2 ), added2 = s2.add( userId1 );
			if( ! added1 || ! added2 )
				return error(CONFLICT);		
			u1.setFollowing(u1.getFollowing() - 1);
			u2.setFollowers(u2.getFollowers() - 1);
		} else {
			boolean removed1 = s1.remove(userId2), removed2 = s2.remove( userId1);
			if( ! removed1 || ! removed2 )
				return error(NOT_FOUND);	
			u1.setFollowing(u1.getFollowing() + 1);
			u2.setFollowers(u2.getFollowers() + 1);
		}
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);
		else
			return ok(s1.contains( userId2 ) && s2.contains( userId1 ));
	}

	@Override
	public Result<Set<String>> getfollowing(String userId){
		if(users.get( userId ) != null) {
			return ok(following.get(userId));
		}
		else
			return error(NOT_FOUND);					

	}
	
	
	private void listen() {
		List<String> topics = Arrays.asList(JavaPosts.POSTS_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {
			Profile p = null;
			switch (key) {
			case "DELETEPOSTUSER":
			    p = getProfile(value).value();
				p.setPosts(p.getPosts() -1);
				break;
			case "CREATEPOST":
				 p = getProfile(value).value();
				p.setPosts(p.getPosts() +1);
				break;
			}
		});
	}

}	


