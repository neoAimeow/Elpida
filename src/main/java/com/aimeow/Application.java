package com.aimeow;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Application {

    public static void main(String args[]) {
        System.out.println("hello world");
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://api.waditu.com");

        httpPost.setHeader("Content-type", "application/json");
        String raw = "{\n" +
                "\t\"api_name\":\"stock_basic\",\n" +
                "\t\"token\":\"2e70679ed6dcf7f5adf2747f8caa6721c27dc77c910c6954e4936229\",\n" +
                "\t\"params\":{\n" +
                "\t\t\"list_status\":\"L\"\n" +
                "\t},\n" +
                "\t\"field\":\"\"\n" +
                "\t\n" +
                "}";
        httpPost.setEntity(new StringEntity(raw, ContentType.DEFAULT_TEXT));

        try {
            // 执行请求

            CloseableHttpResponse response = httpclient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }

            if (response != null) {
                response.close();
            }
            httpclient.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
