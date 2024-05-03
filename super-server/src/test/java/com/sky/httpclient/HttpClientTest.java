package com.sky.httpclient;

import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @projectName: super-takeout
 * @package: com.sky.httpclient
 * @className: HttpClientTest
 * @author: 749291
 * @description: TODO
 * @date: 4/30/2024 18:43
 * @version: 1.0
 */

@SpringBootTest
public class HttpClientTest {
    /**
     * 测试HttpClient发送Get请求
     */


    @Test
    public void testHttpClientGet() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet("http://localhost:7492/user/shop/status");

        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 获取状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode);

        // 获取响应体
        String string = EntityUtils.toString(response.getEntity());
        System.out.println("body = " + string);

        response.close();
        httpClient.close();
    }


    /**
     * 测试HttpClient发送Post请求
     */
    @Test
    public void testHttpClientPost() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://localhost:7492/admin/employee/login");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", "admin");
        jsonObject.addProperty("password", "123456");

        StringEntity stringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
        stringEntity.setContentType("application/json");

        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        // 获取状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("statusCode = " + statusCode);

        // 获取响应体
        String string = EntityUtils.toString(response.getEntity());
        System.out.println("body = " + string);

        response.close();
        httpClient.close();
    }

}

