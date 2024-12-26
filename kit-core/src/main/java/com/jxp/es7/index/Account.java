package com.jxp.es7.index;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

/**
 * @author jiaxiaopeng
 * Created on 2024-12-26 17:13
 */

@Data
@Document(indexName = "account")
public class Account {
    @Id
    private String id;
    // 解决ES中字段与实体类字段不一致的问题
    @Field(name = "account_number", type = FieldType.Long)
    private Long accountNumber;
    @Field(type = FieldType.Text)
    private String address;
    @Field(type = FieldType.Integer)
    private Integer age;
    @Field(type = FieldType.Long)
    private Long balance;
    @Field(type = FieldType.Text)
    private String city;
    @Field(type = FieldType.Text)
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
}
