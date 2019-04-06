package microgram.impl.srv.soap;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapPosts;
import microgram.impl.srv.java.JavaPosts;

//Make this class concrete.
public class _TODO_PostsWebService extends SoapService implements SoapPosts {

	final Posts impl;
	
	protected _TODO_PostsWebService() {
		this.impl = new JavaPosts();
	}

	@Override
	public Post getPost( String postId ) throws MicrogramException {
		return super.resultOrThrow( impl.getPost(postId));
	}

	@Override
	public void deletePost(String postId) throws MicrogramException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createPost(Post post) throws MicrogramException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLiked(String postId, String userId) throws MicrogramException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void like(String postId, String userId, boolean isLiked) throws MicrogramException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getPosts(String userId) throws MicrogramException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getFeed(String userId) throws MicrogramException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
