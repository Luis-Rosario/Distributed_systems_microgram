package microgram.impl.clt.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import discovery.Discovery;
import microgram.api.java.Media;
import microgram.api.java.Result;
import microgram.api.rest.RestMediaStorage;
import microgram.api.rest.RestPosts;

public class RestMediaClient extends RestClient implements Media {

	public static final String SERVICE = "Microgram-MediaStorage";
	

	public RestMediaClient() throws IOException, URISyntaxException {
		this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
	}
	
	public RestMediaClient(URI uri) {
		super(uri, RestMediaStorage.PATH);
	}
	

	@Override
	public Result<String> upload(byte[] bytes) {
		Response r = target
				.request()
				.post( Entity.entity( bytes, MediaType.APPLICATION_OCTET_STREAM));
		
		return super.responseContents(r, Status.OK, new GenericType<String>(){});	
	}

	@Override
	public Result<byte[]> download(String id) {
		Response r = client.target(id)
				.request()
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		return super.responseContents(r, Status.OK, new GenericType<byte[]>(){});	
	}

	@Override
	public Result<Void> delete(String id) {
		Response r = client.target(id)
				.request()
				.delete( );
		
		
		return super.verifyResponse(r, Status.NO_CONTENT);	
	}

}
