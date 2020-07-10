use OnlineEditor;

drop table if exists plugins_info;

/*==============================================================*/
/* Table: plugins_info                                          */
/*==============================================================*/
create table plugins_info
(
  plugin_id            int not null auto_increment comment '插件ID',
  plugin_name          varchar(64) comment '插件名称',
  plugin_path          varchar(64) comment '插件路径',
  create_time          datetime not null default 'current timestamp' comment '插件创建时间',
  last_update_time     datetime comment '插件上次更新时间',
  plugin_author        varchar(64) comment '插件作者',
  plugin_group_name    smallint(4) comment '插件所属的插件组',
  primary key (plugin_id)
);

alter table plugins_info comment '插件信息表';
