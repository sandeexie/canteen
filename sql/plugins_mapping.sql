use OnlineEditor;

drop table if exists plugins_mapping;

/*==============================================================*/
/* Table: plugins_mapping                                       */
/*==============================================================*/
create table plugins_mapping
(
  plugins_group_id     smallint(4) not null comment '插件ID',
  plugins_group_name   varchar(64) not null comment '插件组名称',
  primary key (plugins_group_id)
);

alter table plugins_mapping comment '插件映射信息表';
