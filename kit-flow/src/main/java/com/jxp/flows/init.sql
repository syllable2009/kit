create table node
(
    id                 bigint(20) unsigned auto_increment comment '自增主键' primary key,
    update_time        datetime             DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP comment '更新时间',
    create_time        datetime             DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    user_id            varchar(36) not null DEFAULT '' comment '用户id',
    state              int(4) not null DEFAULT 0 comment '留言状态，0=未领取，1=已领取未回复，2=已回复，3=已废弃',
    message            text null comment '留言内容',
    sources            text null comment '资源列表,英文逗号分隔',
    reply_message      text null comment '客服回复的消息',
    claim_assistant_id varchar(36) null DEFAULT '' comment '领取/回复/废弃的客服id',
    assistant_group_id varchar(36) null default '' null comment '客服组id',
    message_server_id  varchar(36) null DEFAULT '' null comment '消息号',
    reply_time         datetime null DEFAULT null comment '回复时间',
    claim_time         datetime null DEFAULT null comment '领取时间',
    discard_time       datetime null DEFAULT null comment '作废时间',
    origin             varchar(36) not null DEFAULT '' comment '来源',
    KEY                idx_state_group_id ( state, assistant_group_id),
    KEY                idx_state_claim_assistant_id ( state, claim_assistant_id)
) comment 'node表' default charset = utf8mb4
                   COLLATE = utf8mb4_unicode_ci
                   engine = InnoDB;