# 元数据
{
"_index": "user",
"_type": "_doc",
"_id": "l0D6UmwBn8Enzbv1XLz0",
"_score": 1.6943597,
"_source": {
"user": "mj",
"sex": "男",
"age": "18"
}
}
_index：文档所属的索引名称
_type：文档所属的类型名
_id：文档的唯一标识
_version：文档的版本信息
_score：文档的相关性打分
_source：文档的原始 JSON 内容

{
"settings": {
"index": {
// 设置主分片数
"number_of_shards": "1",
"auto_expand_replicas": "0-1",
"provided_name": "kibana_sample_data_logs",
"creation_date": "1564753951554",
// 设置副本分片数
"number_of_replicas": "1",
"uuid": "VVMLRyw6TZeSfUvvLNYXEw",
"version": {
"created": "7010099"
}
}
}
}

interface Query
abstract class BaseQuery
# CriteriaQuery：CriteriaQuery 是基于 Criteria API 的查询构建方式。
CriteriaQuery criteriaQuery = new CriteriaQuery(new Criteria("field").is("value"));
# NativeQuery：‌NativeQuery 是一种用于执行原生 Elasticsearch 查询的方式。
NativeQuery nativeQuery = new NativeQuery("{\"query\": {\"match\": {\"field\": \"value\"}}}");
# StringQuery：StringQuery 是用于执行字符串形式的查询。
StringQuery stringQuery = new StringQuery("{\"query\": {\"match\": {\"field\": \"value\"}}}");
# NativeSearchQuery：‌NativeSearchQuery‌是通过Spring Data Elasticsearch提供的NativeSearchQuery类来构建查询。
NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
.withQuery(QueryBuilders.matchQuery("field", "value"))
.withPageable(PageRequest.of(0, 10))
.build();
