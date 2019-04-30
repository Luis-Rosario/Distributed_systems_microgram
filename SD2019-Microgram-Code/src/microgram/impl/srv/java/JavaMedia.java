package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kakfa.KafkaPublisher;
import kakfa.KafkaSubscriber;
import kakfa.KafkaUtils;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";
	private static  final long MIN = 60000;
	private static String SERVICE = "Microgram-MediaStorage";
	public static final String MEDIA_STORAGE_EVENTS = "Microgram-MediaStorageEvents";

	enum MediaEventKeys {
		UPLOAD, DOWNLOAD, DELETE
	};

	final KafkaPublisher kafka;
	protected Map<String,Long> eventCache = new ConcurrentHashMap<>();

	public JavaMedia() {
		new File(ROOT_DIR).mkdirs();

		this.kafka = new KafkaPublisher();

		KafkaUtils.createTopics(Arrays.asList(JavaMedia.MEDIA_STORAGE_EVENTS));

		new Thread(() -> {
			listenToSuccessProfiles();
		}).start();
		new Thread(() -> {
			listenToSuccessPosts();
		}).start();

		new Thread(() ->  {
			for(;;) {
				try {
					Thread.sleep(MIN);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				updateCache();
			}
		}).start();
	}

	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			String id = Hash.of(bytes);
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);

			if (filename.exists())
				return Result.error(CONFLICT);

			Files.write(filename.toPath(), bytes);

			long receiveTime = System.currentTimeMillis();
			eventCache.put(id, receiveTime);

			return Result.ok(id);

		} catch (Exception x) {
			x.printStackTrace();
			return error(INTERNAL_ERROR);
		}
	}

	@Override
	public Result<byte[]> download(String id) {
		try {
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);
			if (filename.exists()) {
				kafka.publish(MEDIA_STORAGE_EVENTS, MediaEventKeys.DELETE.name(), id);
				return Result.ok(Files.readAllBytes(filename.toPath()));
			}
			else
				return Result.error(NOT_FOUND);
		} catch (Exception x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
	}


	@Override
	public Result<Void> delete(String id) {
		try {
			File file = new File (ROOT_DIR + id + MEDIA_EXTENSION);
			if(file.delete())
				return Result.ok();
			else {
				return Result.error(NOT_FOUND);
			}		 
		}
		catch(NullPointerException x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
		catch(SecurityException x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
		catch(Exception x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
	}

	private  void listenToSuccessProfiles() {
		List<String> topics = Arrays.asList(JavaProfiles.PROFILES_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);

		subscriber.consume((topic, key, value) -> {	
			switch (key) {
			case "SUCCESS":
				removeFromCache(value);
			}

		});

	}

	private  void listenToSuccessPosts() {
		List<String> topics = Arrays.asList(JavaPosts.POSTS_EVENTS);

		KafkaSubscriber subscriber = new KafkaSubscriber(topics);

		subscriber.consume((topic, key, value) -> {	
			switch (key) {
			case "SUCCESS":
				removeFromCache(value);
			}

		});
	}

	private void updateCache() { 
		for (String key:eventCache.keySet()) {
			long eventTime= eventCache.get(key);
			if(eventTime + MIN > System.currentTimeMillis()) {
				delete(key);
			}
		}
	}

	private void removeFromCache(String id) {
		eventCache.remove(id);
	}
}
