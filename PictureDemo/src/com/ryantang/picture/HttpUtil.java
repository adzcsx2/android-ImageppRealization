package com.ryantang.picture;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class HttpUtil {

	public static final String imageppUrl = "http://apicn.imageplusplus.com/analyze";

	public static OkHttpClient client = new OkHttpClient();

	public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType
			.parse("text/x-markdown; charset=utf-8");

	{
		client.setConnectTimeout(10, TimeUnit.SECONDS);
	}
	private static final MediaType MEDIA_TYPE_PNG = MediaType
			.parse("image/png");

	public static String request_get(String url) {

		Request request = new Request.Builder().url(url).build();
		try {
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String request_post_image(File bitmap_file) {
		RequestBody requestBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addFormDataPart("api_key", "acbbcb4a61d4e49e30787345d0eb1616")
				.addFormDataPart("api_secret",
						"e16c6329875c8c2281d59c3c8200efc6")
				.addFormDataPart("img", bitmap_file.getName(),
						RequestBody.create(MEDIA_TYPE_PNG, bitmap_file))
				.build();

		Request request = new Request.Builder().url(imageppUrl)
				.post(requestBody).build();
		try {
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String X(float x) {
		return "x=" + x + "&";
	}

	public static String Y(float y) {
		return "y=" + y;
	}

}
