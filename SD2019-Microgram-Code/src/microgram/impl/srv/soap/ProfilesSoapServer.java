package microgram.impl.srv.soap;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.ws.Endpoint;

import com.sun.net.httpserver.HttpServer;

import discovery.Discovery;
import microgram.api.soap.SoapProfiles;
import utils.IP;

@SuppressWarnings("restriction")
public class ProfilesSoapServer {
	private static Logger Log = Logger.getLogger(ProfilesSoapServer.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}

	public static final int PORT = 8887;
	public static final String SERVICE = "Microgram-Profiles";
	public static String SERVER_BASE_URI = "http://%s:%s/soap";
	public static String SOAP_BASE_PATH = "/soap/" + SoapProfiles.NAME;		


	public static void main(String[] args) throws Exception {
		Log.setLevel( Level.FINER );

		String ip = IP.hostAddress();
		String serverURI = String.format(SERVER_BASE_URI, ip, PORT);


		HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

		Endpoint soapEndpoint = Endpoint.create(new ProfilesWebService());
		
		soapEndpoint.publish(server.createContext(SOAP_BASE_PATH));
		server.setExecutor( Executors.newCachedThreadPool() );
		server.start();

		Log.info(String.format("%s Soap Server ready @ %s\n", SERVICE, ip + ":" + PORT));

		new Thread( () -> {
			Discovery.announce(SERVICE, serverURI);   
		}).start();

	}
}
