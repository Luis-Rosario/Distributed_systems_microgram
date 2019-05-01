package microgram.impl.srv.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import discovery.Discovery;
import microgram.impl.srv.rest.utils.GenericExceptionMapper;
import microgram.impl.srv.rest.utils.PrematchingRequestFilter;
import utils.IP;


public class ProfilesRestServer {
	private static Logger Log = Logger.getLogger(ProfilesRestServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}
	
	public static final int PORT = 7779;
	public static final String SERVICE = "Microgram-Profiles";
	public static String SERVER_BASE_URI = "http://%s:%s/rest";
	
	public static void main(String[] args) throws Exception {

		Log.setLevel( Level.FINER );
		int n = -1;
		for(int i = 0; i< args.length; i++) {
			if(args[i].equals("-profiles")) {
				n = Integer.parseInt(args[++i]);
				System.err.println("wrwerwe");
			}	
			System.err.println("indx:"+ i +"-> " +args[i]);
		}
		
		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
		
		ResourceConfig config = new ResourceConfig();
		
		Discovery.announce(SERVICE, serverURI);
		
		if ( n == -1)
		config.register(new RestProfilesResources(serverURI));  
		else
		config.register(new RestProfilesResources(serverURI , n)); 
		
		config.register(new GenericExceptionMapper());
		config.register(new PrematchingRequestFilter());
		
		JdkHttpServerFactory.createHttpServer( URI.create(serverURI.replace(ip, "0.0.0.0")), config);

		Log.info(String.format("%s Server ready @ %s\n",  SERVICE, serverURI));
		
		 System.err.println("n = " + n);
		
	}
}
