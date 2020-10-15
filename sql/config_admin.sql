use OnlineEditor;

drop table if exists config_admin;

/*==============================================================*/
/* Table: config_admin                                          */
/*==============================================================*/
create table config_admin
(
  user_id              int(11) not null comment '用户ID',
  config_id            int(11) not null comment '配置ID',
  auth_code            tinyint(1) not null default 1 comment '是否授权配置权限：
            0 无权限
            1 有读取权限
            2 有读取,修改权限'
);

alter table config_admin comment '配置权限控制表';
