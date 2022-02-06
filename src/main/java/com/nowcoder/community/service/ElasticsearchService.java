package com.nowcoder.community.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Discuss;
import com.nowcoder.community.entity.SearchResult;
import com.nowcoder.community.mapper.elasticsearch.DiscussRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 如何实现es的搜索？为实现es搜索，需要
 * 1. 将数据存放入ES服务器中
 * 2. 删帖的时候异步在ES服务器上删除帖子
 * 3. 增贴的时候异步在ES服务器上增加帖子*/
public class ElasticsearchService {
    @Autowired
    private DiscussRepository repository;

    @Autowired
    private RestHighLevelClient client;

    @Qualifier("client")
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //存储数据
    public void saveDiscuss(Discuss discuss){
        repository.save(discuss);
    }
    //es服务器中多次存储就是修改

    //删除数据
    public void deleteDiscuss(int id){
        repository.deleteById(id);
    }

    //高亮搜索
    public SearchResult searchDiscuss(String keyword, int current, int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest("discusspost");//discusspost是索引名，就是表名

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮显示的区域
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .sort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                //查询结果分页
                .from(current)
                .size(limit)
                .highlighter(highlightBuilder);
        // 查询结果
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        long total = searchResponse.getHits().getTotalHits().value;
        List<Discuss> list = new LinkedList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Discuss discussPost = JSONObject.parseObject(hit.getSourceAsString(), Discuss.class);

            // 处理高亮显示的结果
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                discussPost.setTitle(titleField.getFragments()[0].toString());
            }
            HighlightField contentField = hit.getHighlightFields().get("content");
            if (contentField != null) {
                discussPost.setContent(contentField.getFragments()[0].toString());
            }
            //System.out.println(discussPost);
            list.add(discussPost);
        }
        return new SearchResult(list, total);
    }

}
