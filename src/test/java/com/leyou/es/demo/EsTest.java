package com.leyou.es.demo;

import com.leyou.es.pojo.Item;
import com.leyou.es.repository.ItemRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
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

    /**
     * {
     *   "took": 8,
     *   "timed_out": false,
     *   "_shards": {
     *     "total": 1,
     *     "successful": 1,
     *     "skipped": 0,
     *     "failed": 0
     *   },
     *   "hits": {
     *     "total": 5,
     *     "max_score": 1,
     *     "hits": [
     *       {
     *         "_index": "heima_tc",
     *         "_type": "item",
     *         "_id": "1",
     *         "_score": 1,
     *         "_source": {
     *           "id": 1,
     *           "title": "小米手机7",
     *           "category": "手机",
     *           "brand": "小米",
     *           "price": 3299,
     *           "images": "http://image.leyou.com/13123.jpg"
     *         }
     *       },
     *       {
     *         "_index": "heima_tc",
     *         "_type": "item",
     *         "_id": "2",
     *         "_score": 1,
     *         "_source": {
     *           "id": 2,
     *           "title": "坚果手机R1",
     *           "category": "手机",
     *           "brand": "锤子",
     *           "price": 3699,
     *           "images": "http://image.leyou.com/13123.jpg"
     *         }
     *       },
     *       {
     *         "_index": "heima_tc",
     *         "_type": "item",
     *         "_id": "3",
     *         "_score": 1,
     *         "_source": {
     *           "id": 3,
     *           "title": "华为META10",
     *           "category": "手机",
     *           "brand": "华为",
     *           "price": 4499,
     *           "images": "http://image.leyou.com/13123.jpg"
     *         }
     *       },
     *       {
     *         "_index": "heima_tc",
     *         "_type": "item",
     *         "_id": "4",
     *         "_score": 1,
     *         "_source": {
     *           "id": 4,
     *           "title": "小米Mix2S",
     *           "category": "手机",
     *           "brand": "小米",
     *           "price": 4299,
     *           "images": "http://image.leyou.com/13123.jpg"
     *         }
     *       },
     *       {
     *         "_index": "heima_tc",
     *         "_type": "item",
     *         "_id": "5",
     *         "_score": 1,
     *         "_source": {
     *           "id": 5,
     *           "title": "荣耀V10",
     *           "category": "手机",
     *           "brand": "华为",
     *           "price": 2799,
     *           "images": "http://image.leyou.com/13123.jpg"
     *         }
     *       }
     *     ]
     *   },
     *   "aggregations": {
     *     "popular_brand": {
     *       "doc_count_error_upper_bound": 0,
     *       "sum_other_doc_count": 0,
     *       "buckets": [
     *         {
     *           "key": "华为",
     *           "doc_count": 2
     *         },
     *         {
     *           "key": "小米",
     *           "doc_count": 2
     *         },
     *         {
     *           "key": "锤子",
     *           "doc_count": 1
     *         }
     *       ]
     *     }
     *   }
     * }
     */
    @Test
    public void testAgg(){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.addAggregation(AggregationBuilders.terms("popularBrand").field("brand"));
        AggregatedPage<Item> result = template.queryForPage(queryBuilder.build(), Item.class);
        //解析items
        //获取聚合
        Aggregations aggregations = result.getAggregations();
        //获取指定名称的聚合
        //Aggregation popularBrand = aggregations.get("popularBrand");
        StringTerms popularBrand = aggregations.get("popularBrand");
        //获取桶
        List<StringTerms.Bucket> buckets = popularBrand.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            System.out.println("bucket.getKeyAsString()= "+bucket.getKeyAsString());
            System.out.println("bucket.getDocCount()= "+bucket.getDocCount());
        }

    }
}
