package microgram.impl.srv.rest;

import java.util.logging.Logger;

import microgram.api.java.Media;
import microgram.api.rest.RestMediaStorage;
import microgram.impl.srv.java.JavaMedia;

public class RestMediaResources extends RestResource implements RestMediaStorage {
	private static Logger Log = Logger.getLogger(RestMediaResources.class.getName());
	
	final Media impl;
	final String baseUri;
	
	public RestMediaResources(String serverUri) {
		this.baseUri = serverUri + RestMediaStorage.PATH;
		impl = new JavaMedia();
	}

	@Override
	public String upload(byte[] bytes) {
		return super.resultOrThrow(impl.upload(bytes));
	}

	@Override
	public byte[] download(String id) {
		return super.resultOrThrow(impl.download(id));
	}

	@Override
	public void delete(String id) {
	 super.resultOrThrow(impl.delete(id));
		
	}

}
