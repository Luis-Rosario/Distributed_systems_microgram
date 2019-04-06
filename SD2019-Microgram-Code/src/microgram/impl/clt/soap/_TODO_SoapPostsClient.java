package microgram.impl.clt.soap;

import discovery.Discovery;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapPosts;
import microgram.impl.clt.soap.SoapClient;

public class _TODO_SoapPostsClient
extends SoapClient
implements Posts {
    public static final String SERVICE = "Microgram-Posts";
    SoapPosts impl;

    public _TODO_SoapPostsClient() throws IOException, URISyntaxException {
        this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
    }

    public _TODO_SoapPostsClient(URI serverUri) {
        super(serverUri);
    }

    public Result<Post> getPost(String postId) {
        return super.tryCatchResult(() -> this.impl().getPost(postId));
    }

    private SoapPosts impl() {
        return this.impl;
    }

    public Result<String> createPost(Post post) {
        return null;
    }

    public Result<Void> deletePost(String postId) {
        return null;
    }

    public Result<Void> like(String postId, String userId, boolean isLiked) {
        return null;
    }

    public Result<Boolean> isLiked(String postId, String userId) {
        return null;
    }

    public Result<List<String>> getPosts(String userId) {
        return null;
    }

    public Result<List<String>> getFeed(String userId) {
        return null;
    }
}
