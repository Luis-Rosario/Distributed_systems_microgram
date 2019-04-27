package garbage_colector;

import java.util.Arrays;
import java.util.List;

import kakfa.KafkaSubscriber;
import microgram.impl.srv.java.JavaMedia;


public class Garbage_Colector {
	private static long MIN = 60000;

	public Garbage_Colector() {
		
	}
	
	
	
	
	public void treatEvents() {
		List<String> topics = Arrays.asList(JavaMedia.MEDIA_STORAGE_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {
			switch (key) {
			case "UPLOAD" : 
				doSomething(value);

			}
		});
		
	}
	private void doSomething(String id) {
		long start =  System.currentTimeMillis();
		
		List<String> topics = Arrays.asList(JavaMedia.MEDIA_STORAGE_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);
		
		subscriber.consume((topic, key, value) -> {
			if (System.currentTimeMillis() -  start < 5*MIN)
				
			switch (key) {
			case "UPLOAD" : 
				doSomething(value);

			}
		
		});
	}
	
}
