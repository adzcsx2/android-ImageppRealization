package com.ryantang.picture;


import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kaifa on 2015/12/2.
 */
public class YouDaoTranslationUtil {

    //有道翻译API
    private static final String url = "http://fanyi.youdao.com/openapi.do?keyfrom=hahabot&key=687163605&type=data&doctype=json&version=1.1&q=";

    /**
     * 异步的翻译请求
     * @param content
     * @param onBaiduTranslationListener
     */
    public static void getTranslationResult(String content, OnTranslationListener onBaiduTranslationListener) {
        try {
            content = URLEncoder.encode(content, "utf-8");
            URL getUrl = new URL(url + content);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();
            // 取得输入流，并使用Reader读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            // 断开连接
            connection.disconnect();
            String result = getJsonResult(sb.toString());
            onBaiduTranslationListener.onResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 同步的翻译请求
     * @param content
     */
    public static String getTranslationResult(String content) {
    	try {
    		content = URLEncoder.encode(content, "utf-8");
    		URL getUrl = new URL(url + content);
    		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    		connection.connect();
    		// 取得输入流，并使用Reader读取
    		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
    		StringBuffer sb = new StringBuffer();
    		String line = "";
    		while ((line = reader.readLine()) != null) {
    			sb.append(line);
    		}
    		reader.close();
    		// 断开连接
    		connection.disconnect();
    		String result = getJsonResult(sb.toString());
    		return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return "";
    }

    private static String getJsonResult(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray JSONArray_translation = object.getJSONArray("translation");
        String str_translation = JSONArray_translation.getString(0);
        Log.e("str_translation", str_translation);
        return str_translation;
    }




    public interface OnTranslationListener {
        void onResult(String content);
    }
}
