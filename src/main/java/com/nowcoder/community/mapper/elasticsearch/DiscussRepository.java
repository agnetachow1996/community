package com.nowcoder.community.mapper.elasticsearch;

import com.nowcoder.community.entity.Discuss;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//repository是spring提供的专门用于数据访问的注解
//类型是<要搜索的内容数据类型，主键的数据类型>
@Repository
public interface DiscussRepository extends ElasticsearchRepository<Discuss,Integer> {
}
