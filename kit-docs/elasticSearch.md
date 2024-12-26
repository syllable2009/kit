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
