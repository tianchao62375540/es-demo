package com.leyou.es.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Auther: tianchao
 * @Date: 2019/11/16 22:47
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//索引名字 ‘heima_tc’ 类型 ‘item’ 分片数量 ‘1’ 副本数量 1
@Document(indexName = "heima_tc",type = "item",shards = 1,replicas = 1)
public class Item {
    /**
     * 索引默认为true
     */
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    /**
     * 标题
     */
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;
    /**
     * 分类
     */
    @Field(type = FieldType.Keyword)
    private String category;
    /**
     * 品牌
     */
    @Field(type = FieldType.Keyword)
    private String brand;
    /**
     * 价格
     */
    @Field(type = FieldType.Double)
    private Double price;
    /**
     * 图片地址
     */
    @Field(type = FieldType.Keyword,index = false)
    private String images;
}
