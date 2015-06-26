package com.gogo.okhttp;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by 80107436 on 2015-06-26.
 */
public class BasicTest {

    public String testGet(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public static void main(String[] args) throws Exception {
        BasicTest bt = new BasicTest();
        System.out.println(bt.testGet("http://www.baidu.com"));
    }

}
