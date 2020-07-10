use OnlineEditor;

drop table if exists sql_admin;

/*==============================================================*/
/* Table: sql_admin                                             */
/*==============================================================*/
create table sql_admin
(
  user_id              int(11) not null comment '用户ID',
  sql_id               int(11) not null comment 'Sql ID全局唯一编号',
  auth_code            tinyint(1) not null default 1 comment '是否授权：
            0 无权限
            1有读取权限
            2.有读取,修改权限'
);

alter table sql_admin comment 'sql权限控制表';