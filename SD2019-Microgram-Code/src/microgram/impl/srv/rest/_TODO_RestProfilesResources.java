package microgram.impl.srv.rest;


import java.net.URI;
import java.util.List;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;

//Make this class concrete.
public class _TODO_RestProfilesResources extends RestResource implements RestProfiles {

	final Profiles impl;
	
	public _TODO_RestProfilesResources(URI serverUri) {
		this.impl = new JavaProfiles();
	}
	
	@Override
	public Profile getProfile(String userId) {
		return super.resultOrThrow( impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Profile> search(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) {
		// TODO Auto-generated method stub
		return false;
	}	
}
