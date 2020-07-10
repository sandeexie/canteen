use OnlineEditor;

drop table if exists view_admin;

/*==============================================================*/
/* Table: view_admin                                            */
/*==============================================================*/
create table view_admin
(
  user_id              int(11) not null comment '用户ID',
  view_id              int(11) not null comment '视图ID',
  is_authed            tinyint(1) not null default 0 comment '是否授权：0 无权限,1有权限'
);

alter table view_admin comment '用户查看页面的权限控制表';
