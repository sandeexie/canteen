use OnlineEditor;

drop table if exists user;

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
  user_id              int not null auto_increment comment '用户ID,全局唯一',
  user_name            varchar(64) not null comment '用户名称',
  user_status          tinyint(1) not null default 1 comment '用户状态:枚举值
            1: 未登陆
            2: 已登录',
  is_logout            tinyint(1) not null default 0 comment '是否注销:
            0: 未注销
            1: 已经注销',
  created_date         datetime not null default 'current timestamp' comment '用户记录创建时间',
  last_updated         datetime not null comment '上次用户信息更新时间',
  primary key (user_id)
);

alter table user comment '用户表';
