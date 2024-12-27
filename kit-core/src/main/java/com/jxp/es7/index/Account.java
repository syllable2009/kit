package com.jxp.es7.index;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 数据类型决定了如何存储和索引该字段。选择合适的类型有助于提高查询性能。
 * Keyword 类型适合用于精确匹配查询，而 Text 类型适合全文搜索。
 * index指定字段是否被索引false意味着您无法在该字段上执行搜索操作
 * analyzer指定用于文本字段的分析器，对于需要进行分词的字段，使用合适的分析器可以提高查询的准确性。
 * searchAnalyzer，可以为字段指定不同的分析器，用于索引和搜索，以提高搜索效果。
 * store = true字段是存储的（即可以在检索时直接返回）。如果将其设置为 false，则在检索时该字段不会返回，尽管它仍然可以用于查询。
 * docValues = true 是一种用于快速聚合和排序的列式存储格式。对于需要排序或聚合的字段，启用 doc values 可以提升性能。
 * nullValue 对于某些查询场景，指定一个 null 值的替代值可以提高查询的性能，避免出现空值。
 *
 * @author jiaxiaopeng
 * Created on 2024-12-26 17:13
 */

@Data
@Document(indexName = "account")
public class Account {
    @Id
    private String id; //确保此字段在保存前不为空,否则抛出异常
    // 解决ES中字段与实体类字段不一致的问题
    @Field(name = "account_number", type = FieldType.Long, index = false)
    private Long accountNumber;
    @Field(type = FieldType.Text, analyzer = "standard")
    private String address;
    @Field(type = FieldType.Integer)
    private Integer age;
    @Field(type = FieldType.Long)
    private Long balance;
    @Field(type = FieldType.Text)
    private String city;
    @Field(type = FieldType.Text,nullValue = "")
    private String email;
    @Field(type = FieldType.Text)
    private String employer;
    @Field(type = FieldType.Text)
    private String firstname;
    @Field(type = FieldType.Text)
    private String lastname;
    @Field(type = FieldType.Text)
    private String gender;
    @Field(type = FieldType.Text)
    private String state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.date_time) // 指定日期类型和格式
    private Date createTime;
}
