package microgram.impl.srv.rest;


import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;

public class RestProfilesResources extends RestResource implements RestProfiles {

	final String baseUri;
	final Profiles impl;

	public RestProfilesResources(String serverUri) {
		this.impl = new JavaProfiles();
		this.baseUri = serverUri + RestProfiles.PATH;
	}

	@Override
	public Profile getProfile(String userId) {
		return super.resultOrThrow( impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) {
		super.resultOrThrow( impl.createProfile(profile));		
	}

	@Override
	public List<Profile> search(String name) {
		return super.resultOrThrow( impl.search(name));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) {
		super.resultOrThrow( impl.follow(userId1, userId2, isFollowing));

	}

	@Override
	public boolean isFollowing(String userId1, String userId2) {
		return super.resultOrThrow( impl.isFollowing(userId1, userId2));
	}

	@Override
	public void deleteProfile(String userId) {
		super.resultOrThrow( impl.deleteProfile(userId));
		
	}

	@Override
	public Set<String> getfollowing(String userId) {
		return super.resultOrThrow( impl.getfollowing(userId));
	}	
}
