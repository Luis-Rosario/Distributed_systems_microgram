/*package garbage_colector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import kakfa.KafkaUtils;
import microgram.impl.srv.java.JavaMedia;
import microgram.impl.srv.java.JavaPosts;
import microgram.impl.srv.java.JavaProfiles;



public class Garbage_Colector {
	
	protected Map<String,Long> eventCache = new ConcurrentHashMap<>();
	
	private static long MIN = 60000;
	
	public static final String GARBAGE_EVENTS = "Microgram-GCEvents";

	enum GarbageEventKeys {
		DELETEPHOTO,
	};

	final KafkaPublisher kafka;

	public Garbage_Colector() {

		this.kafka = new KafkaPublisher();

		KafkaUtils.createTopics(Arrays.asList(GARBAGE_EVENTS));
		
		new Thread(() -> {
			detectMediaStorageChange();
		}).start();
		new Thread(() -> {
			listenToSuccessProfiles();
		}).start();
		new Thread(() -> {
			listenToSuccessPosts();
		}).start();

			for(;;) {
				try {
					Thread.sleep(MIN);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateCache();
			}
		
	}
	
	
	
	public void detectMediaStorageChange() {
		List<String> topics = Arrays.asList(JavaMedia.MEDIA_STORAGE_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {
			long receiveTime = System.currentTimeMillis();
			eventCache.put(key, receiveTime);
		});
		
	}
	
	public void updateCache() { 
		
		 for (String key:eventCache.keySet()) {
			 long eventTime= eventCache.get(key);
			 if(eventTime + MIN > System.currentTimeMillis()) {
				 kafka.publish(GARBAGE_EVENTS, GarbageEventKeys.DELETEPHOTO.name(), key);
			 }
		 }
	}
	
	public  void listenToSuccessProfiles() {
		List<String> topics = Arrays.asList(JavaProfiles.PROFILES_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {	
			switch (key) {
			case "SUCCESS":
				removeFromCache(value);
			}
		
		});
		
	}
	
	public  void listenToSuccessPosts() {
		List<String> topics = Arrays.asList(JavaPosts.POSTS_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {	
			switch (key) {
			case "SUCCESS":
				removeFromCache(value);
			}
		
		});
		
	}
	
	private void removeFromCache(String id) {
		eventCache.remove(id);
	}
	
	
	
	
}
*/