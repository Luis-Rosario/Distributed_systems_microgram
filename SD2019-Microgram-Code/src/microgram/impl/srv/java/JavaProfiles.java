package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import microgram.api.Profile;
import microgram.api.java.Result;
import microgram.api.java.Result.ErrorCode;
import microgram.impl.srv.rest.RestResource;

public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {

	protected Map<String, Profile> users = new HashMap<>();
	protected Map<String, Set<String>> followers = new HashMap<>();
	protected Map<String, Set<String>> following = new HashMap<>();


	@Override
	public Result<Profile> getProfile(String userId) {
		Profile res = users.get( userId );
		if( res == null ) 
			return error(NOT_FOUND);

		res.setFollowers( followers.get(userId).size() );
		res.setFollowing( following.get(userId).size() );
		return ok(res);
	}
	 
	@Override
	public Result<Void> createProfile(Profile profile) {
		Profile res = users.putIfAbsent( profile.getUserId(), profile );
		if( res != null ) 
			return error(CONFLICT);

		followers.put( profile.getUserId(), new HashSet<>());
		following.put( profile.getUserId(), new HashSet<>());
		return ok();
	}


	// falta ir dar delete aos post deste profile
	@Override
	public Result<Void> deleteProfile(String userId) {
		Profile profileToDelete = users.remove(userId);

		if(profileToDelete != null) {

			Set<String> profileFollows = following.remove(userId);
			Set<String> profileFollowers = followers.remove(userId);

			for(String a:profileFollows) {
				followers.get(a).remove(userId);
			}

			for(String a:profileFollowers) {
				following.get(a).remove(userId);
			}

			return ok();
		}

		else {
			return error(NOT_FOUND);
		}
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		return ok(users.values().stream()
				.filter( p -> p.getUserId().startsWith( prefix ) )
				.collect( Collectors.toList()));
	}

	@Override //updated follower/following count
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {		
		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );
		Profile u1 = users.get( userId1 );
		Profile u2 = users.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);

		if( isFollowing ) {
			boolean added1 = s1.add(userId2 ), added2 = s2.add( userId1 );
			if( ! added1 || ! added2 )
				return error(CONFLICT);		
			u1.setFollowing(u1.getFollowing() - 1);
			u2.setFollowers(u2.getFollowers() - 1);
		} else {
			boolean removed1 = s1.remove(userId2), removed2 = s2.remove( userId1);
			if( ! removed1 || ! removed2 )
				return error(NOT_FOUND);	
			u1.setFollowing(u1.getFollowing() + 1);
			u2.setFollowers(u2.getFollowers() + 1);
		}
		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		Set<String> s1 = following.get( userId1 );
		Set<String> s2 = followers.get( userId2 );

		if( s1 == null || s2 == null)
			return error(NOT_FOUND);
		else
			return ok(s1.contains( userId2 ) && s2.contains( userId1 ));
	}

	public Result<Set<String>> getfollowing(String userId){
		if(users.get( userId ) != null) {
			return ok(following.get(userId));
		}
		else
			return error(NOT_FOUND);					

	}
}	


