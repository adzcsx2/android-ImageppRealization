package com.ryantang.picture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import bean.ImageppBean;
import bean.ImageppTag;

import com.google.gson.Gson;

public class MainActivity extends Activity {

	private static final int TAKE_PICTURE = 0;
	private static final int CHOOSE_PICTURE = 1;

	private static final int SCALE = 5;// 照片缩小比例
	private static final String TAG = "MainActivity";
	private ImageView iv_image = null;
	private String result = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		iv_image = (ImageView) this.findViewById(R.id.img);

		this.findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// showPicturePicker(MainActivity.this);
				Camera camera = Camera.open();
				camera.startPreview();
				camera.takePicture(null, null, new PictureCallback() {

					@SuppressLint("NewApi")
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						// 拍照
						Log.e(TAG, data.length + "");
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						bitmap = BitmapUtil.rotaingImageView(90, bitmap);
						bitmap = BitmapUtil.comp(bitmap);
						iv_image.setImageBitmap(bitmap);
						camera.stopPreview();
						camera.release();
						camera = null;
						// 上传imagepp
						final String path = ImageTools.savePhotoToSDCard(
								bitmap, Environment
										.getExternalStorageDirectory()
										.getAbsolutePath(), String
										.valueOf(System.currentTimeMillis()));
						new Thread(new Runnable() {

							@Override
							public void run() {
								File file = new File(path);
								String string = HttpUtil
										.request_post_image(file);
								Log.e(TAG, string);
								if (TextUtils.isEmpty(string)) {
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(MainActivity.this,
													"网络超时", 0).show();
										}
									});
									Log.e(TAG, "网络超时");
									return;
								}
								Gson gson = new Gson();
								ImageppBean imageppBean = gson.fromJson(string,
										ImageppBean.class);
								if (imageppBean.getResult() == null
										|| imageppBean.getResult().getImage() == null) {
									Log.e(TAG, "未能识别出物体");
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(MainActivity.this,
													"未能识别出物体", 0).show();
										}
									});
									return;
								}
								ArrayList<ImageppTag> imageTag = imageppBean
										.getResult().getImage().get(0)
										.getAttribute().getTag();
								result = "";
								for (ImageppTag imageppTag : imageTag) {
									result += "图片上可能是："
											+ YouDaoTranslationUtil
													.getTranslationResult(imageppTag
															.getValue())
											+ "。置信度："
											+ imageppTag.getConfidence() + "\n";
								}
								Log.e(TAG, result);
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(MainActivity.this,
												result, 0).show();
									}
								});
							}
						}).start();
					}
				});
			}
		});

	}

}
