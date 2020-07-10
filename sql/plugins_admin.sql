use OnlineEditor;

drop table if exists plugins_admin;

/*==============================================================*/
/* Table: plugins_admin                                         */
/*==============================================================*/
create table plugins_admin
(
  user_id              int(11) not null comment '用户ID',
  plugin_group_id      smallint(4) not null comment '插件ID',
  auth_code            tinyint(1) not null default 1 comment '是否授权配置权限：
            0 无权限
            1有读取权限
            2.有读取,修改权限'
);

alter table plugins_admin comment '插件管理器';
