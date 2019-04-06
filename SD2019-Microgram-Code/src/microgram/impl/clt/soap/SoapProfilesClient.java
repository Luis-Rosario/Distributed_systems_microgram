package microgram.impl.clt.soap;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;


public class SoapProfilesClient extends SoapClient implements Profiles {
	public static final String SERVICE = "Microgram-Profiles";

    public SoapProfilesClient() throws IOException, URISyntaxException {
        this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
    }

	public SoapProfilesClient(URI serverUri) {
		super(serverUri);
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		return null;
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		// TODO Auto-generated method stub
		return null;
	}

}
