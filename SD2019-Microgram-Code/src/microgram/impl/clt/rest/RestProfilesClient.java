package microgram.impl.clt.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import microgram.api.rest.RestProfiles;

public class RestProfilesClient extends RestClient implements Profiles {

	public static final String SERVICE = "Microgram-Profiles";

	public RestProfilesClient() throws IOException, URISyntaxException {
		this(Discovery.findUrisOf((String) SERVICE, (int) 1)[0]);
	}

	public RestProfilesClient(URI serverUri) {
		super(serverUri, RestProfiles.PATH);
		
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		Response r = target.path(userId).request().accept(MediaType.APPLICATION_JSON).get();

		
		return super.responseContents(r, Status.OK, new GenericType<Profile>() {
		});
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		Response r = target.request().post(Entity.entity(profile, MediaType.APPLICATION_JSON));

	
		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		Response r = target.path(userId).request().delete();

		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		Response r = target.queryParam("query", prefix).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<List<Profile>>() {
		});
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		Response r = target.path(userId1).path("following").path(userId2).request().accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(isFollowing, MediaType.APPLICATION_JSON));

		return super.verifyResponse(r, Status.NO_CONTENT);
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		Response r = target.path(userId1).path("following").path(userId2).request().accept(MediaType.APPLICATION_JSON)
				.get();

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

	@Override
	public Result<Set<String>> getfollowing(String userId) {
		Response r = target.path("getfollowing").path(userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<Set<String>>() {
		});
	}

	@Override
	public Result<Set<String>> getfollowers(String userId) {
		Response r = target.path("getfollowers").path(userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<Set<String>>() {
		});
	}

	@Override
	public Result<List<Profile>> localsearch(String prefix) {
		Response r = target.path("localsearch").queryParam("query", prefix).request().accept(MediaType.APPLICATION_JSON)
				.get();

		return super.responseContents(r, Status.OK, new GenericType<List<Profile>>() {
		});
	}

	@Override
	public Result<Boolean> addfollower(String userId1, String userId2) {
		Response r = target.path("addfollower").path(userId1).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userId2, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

	@Override
	public Result<Boolean> removefollower(String userId1, String userId2) {
		Response r = target.path("removefollower").path(userId1).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userId2, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

	@Override
	public Result<Boolean> addfollowing(String userId1, String userId2) {
		Response r = target.path("addfollowing").path(userId1).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userId2, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

	@Override
	public Result<Boolean> removefollowing(String userId1, String userId2) {
		Response r = target.path("removefollowing").path(userId1).request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(userId2, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

}
