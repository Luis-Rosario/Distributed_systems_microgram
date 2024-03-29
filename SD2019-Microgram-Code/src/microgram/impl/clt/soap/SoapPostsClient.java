package microgram.impl.clt.soap;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.soap.SoapPosts;
import microgram.impl.clt.soap.SoapClient;

public class SoapPostsClient
extends SoapClient
implements Posts {
    public static final String SERVICE = "Microgram-Posts";
    SoapPosts impl;
    private static final String WSDL = "?wsdl";


    public SoapPostsClient(URI serverUri) {
        super(serverUri);
    }

    public Result<Post> getPost(String postId) {
        return super.tryCatchResult(() -> this.impl().getPost(postId));
    }

    public Result<String> createPost(Post post) {
    	  return super.tryCatchResult(() -> this.impl().createPost(post));
    }

    public Result<Void> deletePost(String postId) {
        return super.tryCatchVoid(() -> this.impl().deletePost(postId));
    }

    public Result<Void> like(String postId, String userId, boolean isLiked) {
    	  return super.tryCatchVoid(() -> this.impl().like(postId, userId, isLiked));
    }

    public Result<Boolean> isLiked(String postId, String userId) {
    	 return super.tryCatchResult(() -> this.impl().isLiked(postId, userId));
    }

    public Result<List<String>> getPosts(String userId) {
    	 return super.tryCatchResult(() -> this.impl().getPosts(userId));
    }

    public Result<List<String>> getFeed(String userId) {
    	 return super.tryCatchResult(() -> this.impl().getFeed(userId));
    }
    
    private synchronized SoapPosts impl() {
    	if( impl == null ) {
			try {
				QName QNAME = new QName(SoapPosts.NAMESPACE, SoapPosts.NAME);		
				Service service;
				service = Service.create( new URL(super.uri.toString() + "/" + SoapPosts.NAME + WSDL), QNAME);
				impl = service.getPort( microgram.api.soap.SoapPosts.class );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}	
		}
		return impl;

    }
}
