package com.mtr.dam.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Loader {
	
	private static String awsAccessKeyId = ConfigProperties.getTestDataProperties("aws.access.key.id");
	private static String awsSecretAccessKey = ConfigProperties.getTestDataProperties("aws.secret.access.key");
	private static String awsBucket = ConfigProperties.getTestDataProperties("aws.bucket");
	private static S3Loader instance;
	private static AmazonS3Client amazonS3;

	private S3Loader() {
		amazonS3 = new AmazonS3Client(
				new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey));
	}

	public static S3Loader getInstance() {
		if (instance == null) {
			instance = new S3Loader();
		}
		return instance;
	}

	public int download(String prefix, String targetLocation) {
		ListObjectsRequest s3Request = new ListObjectsRequest().withBucketName(awsBucket);
		s3Request.withPrefix(prefix);
		s3Request.setMaxKeys(30000);
		int size = 0, amount = 0;
		do {
			ObjectListing objectListing = amazonS3.listObjects(s3Request);
			size = objectListing.getObjectSummaries().size();
			amount += size;
//			System.out.println("Quantity of loaded files = " + amount);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				String key = objectSummary.getKey();
				if (key.equals(prefix)) continue;
				GetObjectRequest getRequest = new GetObjectRequest(awsBucket, key);
				S3Object object = amazonS3.getObject(getRequest);
				if (key.startsWith(prefix)) key = key.substring(prefix.length());
				saveFile(object.getObjectContent(), key, targetLocation);
			}
			String nextMarker = objectListing.getNextMarker();
			if (nextMarker != null) {
				s3Request.setMarker(nextMarker);
			} else {
				break;
			}
		} while (true);
		return amount;
	}

	private void saveFile(InputStream is, String name, String targetLocation) {
//		String s = "src/main/resources/" + "toUpload/" + targetLocation + "/" + name;
		String s = "src/main/resources/" + targetLocation + "/" + name;
		File f = new File(s);
		BufferedOutputStream fOut = null;
		try {
			fOut = new BufferedOutputStream(new FileOutputStream(f));
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(buffer)) != -1) {
				fOut.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			// log.error("Error while save file to disk", e );
		} finally {
			try {
				fOut.close();
			} catch (IOException e) {
				// log.error("Error while close BufferedOutputStream", e );
			}
			try {
				is.close();
			} catch (IOException e) {
				// log.error("Error while close InputStream", e );
			}
		}

	}
}