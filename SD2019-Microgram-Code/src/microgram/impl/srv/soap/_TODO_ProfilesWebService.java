package microgram.impl.srv.soap;


import java.util.List;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.soap.MicrogramException;
import microgram.api.soap.SoapProfiles;
import microgram.impl.srv.java.JavaProfiles;

//Make this class concrete.
public class _TODO_ProfilesWebService extends SoapService implements SoapProfiles {

	final Profiles impl;
	
	protected _TODO_ProfilesWebService() {
		this.impl = new JavaProfiles();
	}
	
	@Override
	public Profile getProfile( String userId ) throws MicrogramException {
		return super.resultOrThrow( impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) throws MicrogramException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProfile(String userId) throws MicrogramException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Profile> search(String prefix) throws MicrogramException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) throws MicrogramException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) throws MicrogramException {
		// TODO Auto-generated method stub
		return false;
	}
	
}
