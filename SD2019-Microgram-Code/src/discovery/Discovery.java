package discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * <p>A class to perform service discovery, based on periodic service contact endpoint announcements over multicast communication.</p>
 * 
 * <p>Servers announce their *name* and contact *uri* at regular intervals. Clients listen for contacts, until they discovery the requested number
 * of service instances with the given service name.</p>
 * 
 * <p>Service announcements have the following format:</p>
 * 
 * 
 * <p>&lt;service-name-string&gt;&lt;delimiter-char&gt;&lt;service-uri-string&gt;</p>
 */
public class Discovery {
	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		// addresses some multicast issues on some TCP/IP stacks
		System.setProperty("java.net.preferIPv4Stack", "true");
		// summarizes the logging format
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}


	// The pre-aggreed multicast endpoint assigned to perform discovery. 
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 10000;

	// Used separate the two fields that make up a service announcement.
	private static final String DELIMITER = "\t";

	/**
	 * Starts sending service announcements at regular intervals... 
	 * @param  serviceName the name of the service to announce
	 * @param  serviceURI an uri string - representing the contact endpoint of the service being announced
	 * 
	 */
	public static void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", DISCOVERY_ADDR, serviceName, serviceURI));

		byte[] pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		new Thread(() -> {
			try (DatagramSocket ms = new DatagramSocket()) {
				for (;;) {
					ms.send(pkt);
					Thread.sleep(DISCOVERY_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Performs discovery of instances of the service with the given name.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @param  minRepliesNeeded the required number of service replicas to find. 
	 * @return an array of URI with the service instances discovered. Returns an empty, 0-length, array if the service is not found within the alloted time.
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * 
	 */
	
	public static URI[] findUrisOf(String serviceName, int minRepliesNeeded) throws IOException, URISyntaxException {
		
		try( MulticastSocket socket = new MulticastSocket( DISCOVERY_ADDR.getPort() )) {
			socket.joinGroup( DISCOVERY_ADDR.getAddress());


			Set<URI> set =  new HashSet<URI>();
			
			double time = System.currentTimeMillis();
			while( true ) {
				
				byte[] buffer = new byte[500000] ; 
				
				DatagramPacket request = new DatagramPacket( buffer, buffer.length ) ;
				
				if(System.currentTimeMillis() - time > DISCOVERY_TIMEOUT) {
					return null;
				}
				
				socket.receive( request );

				String dat= new String(request.getData(), 0, request.getLength());


				String[] name = dat.split(DELIMITER);

				if(name[0].equals(serviceName)) {
					set.add(new URI(name[1]));

					if(set.size() == minRepliesNeeded) {
						URI[] ur =  set.toArray(new URI[set.size()]);
						return ur;
					}
				}
			}
		}    
	}
}	

