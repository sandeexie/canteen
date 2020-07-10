use OnlineEditor;

drop table if exists view;

/*==============================================================*/
/* Table: view                                                  */
/*==============================================================*/
create table view
(
  view_id              int not null auto_increment comment '视图ID,全局唯一',
  view_name            varchar(64) comment '视图名称',
  view_path            varchar(256) comment '视图资源路径',
  create_date          datetime default 'current timestamp' comment '视图创建时间',
  last_updated_time    datetime default 'current timestamp' comment '上次更新时间',
  is_enabled           tinyint(1) comment '视图是否可用:0 不可使用,1 可以使用',
  primary key (view_id)
);

alter table view comment '前端web视图表';
