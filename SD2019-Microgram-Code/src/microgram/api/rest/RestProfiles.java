package microgram.api.rest;

import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import microgram.api.Profile;

/**
 * REST API of the Profiles service.
 * 
 * Refer to the Java interface for the semantics
 * @author smd
 *
 */
@Path(RestProfiles.PATH)
public interface RestProfiles {

	static final String PATH="/profiles";
	
	@GET
	@Path("/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	Profile getProfile( @PathParam("userId") String userId );
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	void createProfile( Profile profile );
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	List<Profile> search( @QueryParam("query") String name );
	
	@GET
	@Path("/localsearch")
	@Produces(MediaType.APPLICATION_JSON)
	List<Profile> localsearch( @QueryParam("query") String name );
	
	@DELETE
	@Path("/{userId}")
	void deleteProfile( @PathParam("userId") String userId );
	
	
	@PUT
	@Path("/{userId1}/following/{userId2}")
	@Consumes(MediaType.APPLICATION_JSON)
	void follow( @PathParam("userId1") String userId1, @PathParam("userId2") String userId2, boolean isFollowing);
	
	@GET
	@Path("/{userId1}/following/{userId2}")
	boolean isFollowing( @PathParam("userId1") String userId1, @PathParam("userId2") String userId2);
	
	@GET
	@Path("/getfollowing/{userId}")
	Set<String> getfollowing(@PathParam ("userId") String userId);
	
	@GET
	@Path("/getfollowers/{userId}")
	Set<String> getfollowers(@PathParam ("userId") String userId);
		
	@PUT
	@Path("/addfollower/{userId1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	boolean addfollower(@PathParam ("userId1") String userId1 ,String userId2);
	
	@PUT
	@Path("/removefollower/{userId1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	boolean removefollower(@PathParam ("userId1") String userId1 , String userId2);
	
	@PUT
	@Path("/addfollowing/{userId1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	boolean addfollowing(@PathParam ("userId1") String userId1 ,String userId2);
	
	@PUT
	@Path("/removefollowing/{userId1}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	boolean removefollowing(@PathParam ("userId1") String userId1 , String userId2);
}
