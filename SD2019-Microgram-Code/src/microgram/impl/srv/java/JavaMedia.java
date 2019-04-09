package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.INTERNAL_ERROR;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import microgram.api.java.Media;
import microgram.api.java.Result;
import utils.Hash;

public class JavaMedia implements Media {

	private static final String MEDIA_EXTENSION = ".jpg";
	private static final String ROOT_DIR = "/tmp/microgram/";
	private static String SERVICE = "Microgram-Media";

	public JavaMedia() {
		new File(ROOT_DIR).mkdirs();
	}

	@Override
	public Result<String> upload(byte[] bytes) {
		try {
			String id = Hash.of(bytes);
			File filename = new File(ROOT_DIR + id + MEDIA_EXTENSION);

			if (filename.exists())
				return Result.error(CONFLICT);

			Files.write(filename.toPath(), bytes);
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
			if (filename.exists())
				return Result.ok(Files.readAllBytes(filename.toPath()));
			else
				return Result.error(NOT_FOUND);
		} catch (Exception x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
	}

	@Override
	public Result<String> delete(String id) {
		try {
			 File file = new File (ROOT_DIR + id + MEDIA_EXTENSION);
			 if(!file.exists())
				 return Result.error(NOT_FOUND);
			 Files.delete(file.toPath());
			 return Result.ok();
		}
		catch(IOException x) {
			x.printStackTrace();
			return Result.error(INTERNAL_ERROR);
		}
	}
}
