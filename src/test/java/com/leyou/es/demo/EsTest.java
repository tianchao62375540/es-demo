package com.leyou.es.demo;

import com.leyou.es.pojo.Item;
import com.leyou.es.repository.ItemRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: tianchao
 * @Date: 2019/11/16 22:50
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
    /**
     * 聚合时候用 普通增删改查用不到
     */
    @Autowired
    ElasticsearchTemplate template;

    @Autowired
    ItemRepository itemRepository;

    /**
     * 创建索引
     */
    //@Test
    public void testCreate(){
        //1创建索引库
            //1-1分片数量
            //1-2副本数量
            //1-3索引库名字
            //1-4索引名字 ‘heima_tc’ 类型 ‘item’ 分片数量 ‘1’ 副本数量 1
            //1-5@Document(indexName = "heima_tc",type = "item",shards = 1,replicas = 1)

        template.createIndex(Item.class);
        //2映射字段根据实体类 可能不符合要求 因为Elasticsearch是推断的 所以挨个字段加映射信息
        template.putMapping(Item.class);
    }

    /**
     * 测试删除索引
     */
    //@Test
    public void testDel(){
        template.deleteIndex(Item.class);
    }

    /**
     * 新增数据
     */
    @Test
    public void insertIndex(){
        List<Item> list = new ArrayList<>();
        list.add(new Item(1L, "小米手机7", "手机", "小米", 3299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(2L, "坚果手机R1", "手机", "锤子", 3699.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(4L, "小米Mix2S", "手机", "小米", 4299.00, "http://image.leyou.com/13123.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13123.jpg"));
        // 接收对象集合，实现批量新增
        itemRepository.saveAll(list);
    }

    /**
     * 查询
     */
    @Test
    public void testFind(){
        System.out.println("============全查=================");
        Iterable<Item> all = itemRepository.findAll();
        for (Item item : all) {
            System.out.println(item);
        }
        System.out.println("=============查询根据id=================");
        System.out.println(itemRepository.findById(1L));
        System.out.println("=============自定义查询======================");
        List<Item> allByPriceBetween = itemRepository.findAllByPriceBetween(2000d, 3000d);
        allByPriceBetween.stream().forEach(System.out::println);
    }

    /**
     * 查询
     */
    @Test
    public void testQuery(){
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("title","小米手机");
        Iterable<Item> search = itemRepository.search(queryBuilder);
        for (Item item : search) {
            System.out.println(item);
        }
        System.out.println("=========================================");
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //过滤
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","title"},new String[]{}));
        //页码
        nativeSearchQueryBuilder.withPageable(PageRequest.of(1,2));
        //查询条件
        nativeSearchQueryBuilder.withQuery(queryBuilder);
        //排序
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
        Page<Item> search1 = itemRepository.search(nativeSearchQueryBuilder.build());
        System.out.println(search1);

    }
}
