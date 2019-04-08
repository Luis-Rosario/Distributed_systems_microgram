package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.impl.clt.rest.RestProfilesClient;
import utils.Hash;

public class JavaPosts implements Posts {
	private static String SERVICE = "Microgram-Profiles";

	protected Map<String, Post> posts = new HashMap<>();
	protected Map<String, Set<String>> likes = new HashMap<>();
	protected Map<String, Set<String>> userPosts = new HashMap<>();



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
			return ok();
		}
		else
			return error(NOT_FOUND);
	}


	@Override
	public Result<String> createPost(Post post) {
		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, new HashSet<>());

			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = new LinkedHashSet<>());
			posts.add(postId);

			return ok(postId);
		}
		else
			return error(CONFLICT);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Set<String> res = likes.get(postId);
		if (res == null)
			return error( NOT_FOUND );

		if (isLiked) {
			if (!res.add(userId))
				return error( CONFLICT );
		} else {
			if (!res.remove(userId))
				return error( NOT_FOUND );
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
			return error( NOT_FOUND );
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
			return error( NOT_FOUND );
	}

	//pedir feedback
	@Override
	public Result<List<String>> getFeed(String userId) {

		if(userPosts.get(userId) == null) {
			return error(NOT_FOUND);
		}
		else {
			try{
				Profiles profileClient = new RestProfilesClient(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);

				Result<Set<String>> foll = profileClient.getfollowing(userId);
				
				if(foll.isOK()) {
					Set<String> following = foll.value();
					
					List<String> feedPics = new ArrayList<>();
					
					for(String elem: following) {
						for(String pic: userPosts.get(elem)) {
							feedPics.add(pic);
						}	
					}
					return ok(feedPics);
				}
				
				else {
					return error(NOT_FOUND);
				}
			} catch (IOException e) {
				return error( NOT_FOUND );
			} catch (URISyntaxException e) {
				return error( NOT_FOUND );
			}

		}

	}

}
