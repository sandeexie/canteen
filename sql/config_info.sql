use OnlineEditor;

drop table if exists config_info;

/*==============================================================*/
/* Table: config_info                                           */
/*==============================================================*/
create table config_info
(
  config_id            int(11) not null comment '配置ID',
  config_key           varchar(64) not null comment '配置项名称',
  config_value         varchar(64) not null comment '配置项值',
  config_scope         tinyint(1) not null default 1 comment '配置作用域,枚举值:
            1: 单次数据库链接
            2: 单次会话
            3: JVM运行一次的范围',
  primary key (config_id)
);

alter table config_info comment '配置信息表';
