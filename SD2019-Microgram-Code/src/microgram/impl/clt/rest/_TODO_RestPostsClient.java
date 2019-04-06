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

//TODO Make this class concrete
public class _TODO_RestPostsClient extends RestClient implements Posts {
	
	public static final String SERVICE = "Microgram-Posts";

    public _TODO_RestPostsClient() throws IOException, URISyntaxException {
        this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
    }

	public _TODO_RestPostsClient(URI serverUri) {
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Result<Void> deletePost(String postId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Result<List<String>> getPosts(String userId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Result<List<String>> getFeed(String userId) {
		// TODO Auto-generated method stub
		return null;
	}
}
