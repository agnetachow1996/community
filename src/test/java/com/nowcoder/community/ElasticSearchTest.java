package com.nowcoder.community;

import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.service.DiscussService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class) //让测试运行于Spring测试环境
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
@MapperScan("com.nowcoder.community.mapper")
public class ElasticSearchTest {
    @Autowired
    private DiscussService discussService;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void insertData() throws IOException {
        List<Discuss> result = discussService.selectDiscussPosts(0,0,200);
        List<Map<String,Object>> bulk = new ArrayList<>();
        for(int i = 0;i < result.size();i++){
            Discuss discuss = result.get(i);
            Map<String,Object> temp1 = new HashMap<>();
            temp1.put("id",discuss.getId());
            temp1.put("userId",discuss.getUserId());
            temp1.put("title",discuss.getTitle());
            temp1.put("content",discuss.getContent());
            temp1.put("type",discuss.getType());
            temp1.put("status",discuss.getStatus());
            temp1.put("createTime",discuss.getCreateTime());
            temp1.put("commentCount",discuss.getCommentCount());
            temp1.put("score",discuss.getScore());
            bulk.add(temp1);
        }

        BulkRequest bulkRequest = new BulkRequest();
        for(Map<String,Object> item:bulk){
            bulkRequest.add(new IndexRequest("discuss").id(item.get("id").toString()).source(item));
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
}
