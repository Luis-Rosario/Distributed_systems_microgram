package microgram.impl.clt.java;

import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

public class RetryProfilesClient extends RetryClient implements Profiles {

	final Profiles impl;

	public RetryProfilesClient( Profiles impl ) {
		this.impl = impl;	
	}
	
	@Override
	public Result<Profile> getProfile(String userId) {
		return reTry( () -> impl.getProfile(userId));
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		return reTry( () -> impl.createProfile(profile));
	}

	@Override
	public Result<Void> deleteProfile(String userId) {
		return reTry( () -> impl.deleteProfile(userId));
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return reTry( () -> impl.search(prefix));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {
		return reTry( () -> impl.follow(userId1, userId2, isFollowing));
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {
		return reTry( () -> impl.isFollowing(userId1, userId2));
	}

	@Override
	public Result<Set<String>> getfollowing(String userId) {
		return reTry( () -> impl.getfollowing(userId));
	}

	@Override
	public Result<Set<String>> getfollowers(String userId) {
		return reTry( () -> impl.getfollowers(userId));
	}


	@Override
	public Result<List<Profile>> localsearch(String prefix) {
		return reTry( () -> impl.localsearch(prefix));
	}

	@Override
	public Result<Boolean> addfollower(String userId1, String userId2) {
		return reTry( () -> impl.addfollower(userId1,userId2));
	}

	@Override
	public Result<Boolean> removefollower(String userId1, String userId2) {
		return reTry( () -> impl.removefollower(userId1,userId2));
	}

	@Override
	public Result<Boolean> addfollowing(String userId1, String userId2) {
		return reTry( () -> impl.addfollowing(userId1,userId2));
	}

	@Override
	public Result<Boolean> removefollowing(String userId1, String userId2) {
		return reTry( () -> impl.removefollowing(userId1,userId2));
	}
}
