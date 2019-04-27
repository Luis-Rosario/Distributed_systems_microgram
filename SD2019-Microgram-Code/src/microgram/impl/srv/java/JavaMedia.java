package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import kakfa.KafkaPublisher;
import kakfa.KafkaUtils;
import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";
	private static String SERVICE = "Microgram-MediaStorage";

	public static final String MEDIA_STORAGE_EVENTS = "Microgram-MediaStorageEvents";

	enum MediaEventKeys {
		UPLOAD, DOWNLOAD, DELETE
	};
	final KafkaPublisher kafka;
	
	public JavaMedia() {
		new File(ROOT_DIR).mkdirs();

		this.kafka = new KafkaPublisher();

		KafkaUtils.createTopics(Arrays.asList(JavaMedia.MEDIA_STORAGE_EVENTS));
	}

	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			String id = Hash.of(bytes);
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);

			if (filename.exists())
				return Result.error(CONFLICT);

			Files.write(filename.toPath(), bytes);
			
			kafka.publish(MEDIA_STORAGE_EVENTS, MediaEventKeys.UPLOAD.name(), id);
			
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

	// falar com o prof o pq disto n funcionar e dar internal server error
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
}
