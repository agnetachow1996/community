package com.nowcoder.community.util;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class ESClient {
    public static RestHighLevelClient getClient(){
        //创建HttpHost对象
        HttpHost hostHost = new HttpHost("127.0.0.1",9200,"http");
        //创建RestClientBuilder
        RestClientBuilder builder = RestClient.builder(hostHost);
        //创建RestHighLevelClient
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    public static void closeClient(RestHighLevelClient client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }
}
