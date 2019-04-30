package microgram.impl.clt.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.rest.RestPosts;

public class RestPostsClient extends RestClient implements Posts {
	
	public static final String SERVICE = "Microgram-Posts";

    public RestPostsClient() throws IOException, URISyntaxException {
        this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
    }

	public RestPostsClient(URI serverUri) {
		super(serverUri, RestPosts.PATH);
	}

	
	public Result<String> createPost(Post post) {
		Response r = target
				.request()
				.post( Entity.entity( post, MediaType.APPLICATION_JSON));
		
		return super.responseContents(r, Status.OK, new GenericType<String>(){});	
	}


	@Override
	public Result<Post> getPost(String postId) {
	 	Response  r = target.path(postId)
	 						.request()
	 						.accept(MediaType.APPLICATION_JSON) 
	 						.get(); 
				
				
	  return super.responseContents(r, Status.OK, new GenericType<Post>() {});
	}


	@Override
	public Result<Void> deletePost(String postId) {
		Response  r = target.path(postId)
							.request()
							.delete() ;
				
		return super.verifyResponse(r, Status.OK);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		Response  r = target.path( postId )
							.path("likes")
							.path(userId)
							.request()
							.put(Entity.entity(isLiked, MediaType.APPLICATION_JSON)) ; 
		
		return super.verifyResponse(r, Status.OK);
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Response  r = target.path(postId)
							.path("likes")
							.path(userId)
							.request()
							.get();
		
		
		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {});
	}


	@Override
	public Result<List<String>> getPosts(String userId) {
		Response  r = target.path("from")
							.path(userId)
							.request()
							.accept(MediaType.APPLICATION_JSON)
							.get(); 
		
		return super.responseContents(r, Status.OK, new GenericType<List<String>>() {});
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		Response  r = target.path("feed") 
							.path(userId)
							.request()
							.accept(MediaType.APPLICATION_JSON)
							.get(); 
		
		return super.responseContents(r, Status.OK, new GenericType<List<String>>() {});
	}
}
