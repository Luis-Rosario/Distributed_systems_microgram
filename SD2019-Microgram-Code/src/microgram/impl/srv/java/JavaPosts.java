package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import discovery.Discovery;
import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import kakfa.KafkaUtils;
import microgram.api.Post;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.clt.java.ClientFactory;
import utils.Hash;

public class JavaPosts implements Posts {
	public static String SERVICE = "Microgram-Posts";

	protected Map<String, Post> posts = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> likes = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> userPosts = new ConcurrentHashMap<>();

	Profiles profileClient = null;

	public static final String POSTS_EVENTS = "Microgram-PostsEvents";

	enum PostsEventKeys {
		DELETEPOSTID, DELETEPOSTUSER, CREATEPOST, LIKEPOST, SUCCESS,
	};

	final KafkaPublisher kafka;

	public JavaPosts() {
		this.kafka = new KafkaPublisher();
		KafkaUtils.createTopics(Arrays.asList(JavaPosts.POSTS_EVENTS));
		new Thread(() -> {
			listen();
		}).start();

	}

	@Override
	public Result<Post> getPost(String postId) {
		Post res = posts.get(postId);

		if (res != null)
			return ok(res);
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> deletePost(String postId) {
		Post post = posts.get(postId);

		if (post != null) {
			likes.remove(postId);
			Set<String> postsUser = userPosts.get(post.getOwnerId());
			postsUser.remove(postId);
			posts.remove(postId);
			kafka.publish(POSTS_EVENTS, PostsEventKeys.DELETEPOSTUSER.name(), post.getOwnerId());
			kafka.publish(POSTS_EVENTS, PostsEventKeys.DELETEPOSTID.name(), postId);

			return ok();
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<String> createPost(Post post) {

		String ownerId = post.getOwnerId();

		if (profileClient == null) {
			try {
				profileClient = ClientFactory.getProfilesClient(Discovery.findUrisOf((String) JavaProfiles.SERVICE, (int) 1)[0]);

			} catch (IOException e) {

			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if(profileClient.getProfile(ownerId).isOK()) {

			String postId = Hash.of(ownerId, post.getMediaUrl());
			if (posts.putIfAbsent(postId, post) == null) {

				likes.put(postId, new HashSet<>());

				Set<String> posts = userPosts.get(ownerId);
				if (posts == null)
					userPosts.put(ownerId, posts = new LinkedHashSet<>());

				posts.add(postId);

				kafka.publish(POSTS_EVENTS, PostsEventKeys.CREATEPOST.name(), ownerId);
				kafka.publish(POSTS_EVENTS, PostsEventKeys.SUCCESS.name(), post.getMediaUrl());

			}
			return ok(postId);

		}

		else {
			return error(NOT_FOUND);	
		}

	}
	
	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Set<String> res = likes.get(postId);
		if (res == null)
			return error(NOT_FOUND);

		if (isLiked) {
			if (!res.add(userId))
				return error(CONFLICT);
		} else {
			if (!res.remove(userId))
				return error(NOT_FOUND);
		}

		getPost(postId).value().setLikes(res.size());

		return ok();
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);

		if (res != null)
			return ok(res.contains(userId));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		if (profileClient == null)
			try {
				profileClient = ClientFactory.getProfilesClient(Discovery.findUrisOf((String) JavaProfiles.SERVICE, (int) 1)[0]);
			} catch (Exception e) {
				return error(INTERNAL_ERROR);
			}
		Result<Profile> p = profileClient.getProfile(userId);
		if (p.isOK()) {
			Set<String> res = userPosts.get(userId);
			if (res == null)
				return ok(new ArrayList<>());
			else
				return ok(new ArrayList<>(res));
		} else
			return error(NOT_FOUND);
	}


	@Override
	public Result<List<String>> getFeed(String userId) {
		try {
			if (profileClient == null)
				profileClient = ClientFactory.getProfilesClient(Discovery.findUrisOf((String) JavaProfiles.SERVICE, (int) 1)[0]);

			Result<Set<String>> foll = profileClient.getfollowing(userId);

			if (foll.isOK()) {
				Set<String> following = foll.value();

				List<String> feedPics = new ArrayList<>();

				for (String elem : following) {
					if (userPosts.containsKey(elem))
						for (String pic : userPosts.get(elem)) {
							feedPics.add(pic);
						}
				}
				return ok(feedPics);
			}

			else {
				return error(NOT_FOUND);
			}
		} catch (IOException e) {
			return error(INTERNAL_ERROR);
		} catch (URISyntaxException e) {
			return error(INTERNAL_ERROR);
		}

	}

	/**
	 * Method that listens for profile kafka event Delete profile to delete the deleted user's posts and remove his likes from photos
	 */
	private void listen() {
		List<String> topics = Arrays.asList(JavaProfiles.PROFILES_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);

		subscriber.consume((topic, key, value) -> {
			switch (key) {
			case "DELETEPROFILE":
				Result<List<String>> posts = getPosts(value);
				if (posts.isOK()) {
					for (String post : posts.value()) {
						deletePost(post);
					}
				}

				for(Post post: this.posts.values()) {
					for(String userLike: likes.get(post.getPostId())) {
						if(userLike.equals(value)) {
							likes.get(post.getPostId()).remove(userLike);
							post.setLikes(likes.size());
						}
					}
				}
			}
		});
	}
	
}
