package microgram.api.java;

import java.util.List;
import java.util.Set;

import microgram.api.Profile;

/**
 * 
 * Interface for the service that manages Users/Profiles.
 * 
 */
public interface Profiles {

	/**
	 * Obtains a profile
	 * @param userId unique identifier of the requested profile
	 * @return result of (OK,Profile), or NOT_FOUND
	 */
	Result<Profile> getProfile( String userId );

	/**
	 * Creates a profile 
	 * @param profile to be created
	 * @return result of (OK,), or CONFLICT
	 */
	Result<Void> createProfile( Profile profile );

	/**
	 * Delete a profile 
	 * @param userId identifier of the profile to be deleted
	 * @return result of (OK,), or NOT_FOUND
	 */
	Result<Void> deleteProfile( String userId );

	/**
	 * Searches for profiles by prefix of the profile identifier
	 * @param prefix - the prefix used to match identifiers
	 * @return result of (OK, List<Profile>); an empty list if the search yields no profiles
	 */
	Result<List<Profile>> search( String prefix );


	Result<List<Profile>> localsearch( String prefix );

	/**
	 * Causes a profile to follow or stop following another.
	 * 
	 * @param userId1 the profile that will follow or cease to follow the followed profile
	 * @param userId2 the followed profile 
	 * @param isFollowing flag that indicates the desired end status of the operation
	 * @return
	 */
	Result<Void> follow( String userId1, String userId2, boolean isFollowing);

	/**
	 * Checks if a profile is following another or not
	 * 
	 * @param userId1 the follower profile
	 * @param userId2 the followed profile
	 * @return (OK,Boolean), NOT_FOUND if any of the profiles does not exist
	 */
	Result<Boolean> isFollowing( String userId1, String userId2);

	/**
	 * Returns a set with the id's of the users the user follows
	 * @param userId the user that follows
	 * @return (OK,Set<String>), NOT_FOUND if the user does not exist
	 */
	Result<Set<String>> getfollowing(String userId);

	/**
	 *  Returns a set with the id's of the users that follow a given user
	 * @param userId the user to be followed
	 * @return  (OK,Set<String>), NOT_FOUND if the user does not exist
	 */
	Result<Set<String>> getfollowers(String userId);

	/**
	 * Checks if a given user is following another, and follows him if it wasn't already
	 * @param userId1 the user to be followed
	 * @param userId2 the user that follows
	 * @return (OK,Boolean), NOT_FOUND if the user does not exist
	 */
	Result<Boolean> addfollower(String userId1 , String userId2);

	/**
	 * Checks if a given user is following another, and unfollows him if he was
	 * @param userId1 the user that is followed
	 * @param userId2 the user that is following
	 * @return (OK,Boolean), NOT_FOUND if the user does not exist
	 */
	Result<Boolean> removefollower(String userId1 , String userId2);

	/**
	 * Checks if a given user is following another, and follows him if it wasn't already
	 * @param userId1 the user that follows
	 * @param userId2 the user to be followed
	 * @return (OK,Boolean), NOT_FOUND if the user does not exist
	 */
	Result<Boolean> addfollowing(String userId1 , String userId2);

	/**
	 * Checks if a given user is following another, and unfollows him if he was
	 * @param userId1 the user that is following		
	 * @param userId2 the user that is followed
	 * @return (OK,Boolean), NOT_FOUND if the user does not exist
	 */
	Result<Boolean> removefollowing(String userId1 , String userId2);

}
