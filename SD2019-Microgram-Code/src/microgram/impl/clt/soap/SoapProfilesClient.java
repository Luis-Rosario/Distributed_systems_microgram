package microgram.impl.clt.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.soap.SoapProfiles;



public class SoapProfilesClient extends SoapClient implements Profiles {
	public static final String SERVICE = "Microgram-Profiles";
	private static final String WSDL = "?wsdl";
	SoapProfiles impl;

    public SoapProfilesClient() throws IOException, URISyntaxException {
        this(Discovery.findUrisOf((String)SERVICE, (int)1)[0]);
        
    }

	public SoapProfilesClient(URI serverUri) {
		super(serverUri);
		
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		  return super.tryCatchResult(() -> this.impl().getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		  return super.tryCatchVoid(() -> this.impl().createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		  return super.tryCatchVoid(() -> this.impl().deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		  return super.tryCatchResult(() -> this.impl().search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		  return super.tryCatchVoid(() -> this.impl().follow(userId1,userId2, isFollowing));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		  return super.tryCatchResult(() -> this.impl().isFollowing(userId1,userId2));
	}

	@Override
	public Result<Set<String>> getfollowing(String userId) {
		 return super.tryCatchResult(() -> this.impl().getfollowing(userId));
	}
	
	private SoapProfiles impl() {
		if( impl == null ) {
			try {
				QName QNAME = new QName(SoapProfiles.NAMESPACE, SoapProfiles.NAME);		
				Service service;
				service = Service.create( new URL(super.uri.toString() + WSDL), QNAME);
				impl = service.getPort( microgram.api.soap.SoapProfiles.class );
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}	
		}
		return impl;
    }

}
