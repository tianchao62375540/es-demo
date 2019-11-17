package com.leyou.es.repository;

import com.leyou.es.pojo.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Auther: tianchao
 * @Date: 2019/11/16 23:23
 * @Description:
 */
public interface ItemRepository extends ElasticsearchRepository<Item,Long> {

    List<Item> findAllByPriceBetween(Double begin,Double end);
}
