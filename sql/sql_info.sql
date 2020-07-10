use OnlineEditor;

drop table if exists sql_info;

/*==============================================================*/
/* Table: sql_info                                              */
/*==============================================================*/
create table sql_info
(
  sql_id               int not null auto_increment comment 'Sql ID,全局唯一',
  sql_type             tinyint(1) default 0 comment 'SQL 类型： 枚举值
            0：MySQL
            1:   Oracle
            2:   Postgre
            3:   SQLServer
            4:   Hive
            5:   SparkSQL
            6:   FlinkSQL
            7:   Redis
            8:   ElasticSeach
            9:   MongoDB
            10: HBase
            11: TiDB',
  sql_text             text comment 'SQL执行体',
  created_time         datetime not null default 'current timestamp' comment 'SQL记录创建时间',
  start_time           datetime comment 'SQL执行起始时间',
  finish_time          datetime comment 'SQL执行结束时间',
  result_code          tinyint(1) default 0 comment 'SQL执行结果:
            0:  未定义(没运行完成)
            1:  成功执行
            2:  执行失败',
  retry_times          tinyint(1) default 0 comment 'SQL重试次数(默认0)',
  is_slow_query        tinyint(1) default 0 comment '是否是慢查询:1 是 0 不是',
  peak_memory_cost     long default '0' comment '峰值内存使用量',
  result_size          long default '0' comment '执行结果大小(单位: 字节)',
  operate_type         tinyint(1) default 0 comment '操作类型: 0 查找 1 更新',
  is_error_occur       tinyint(1) default 0 comment '是否出现了异常 0 未出现 1出现异常',
  error_reason         text comment '出现异常的原因',
  primary key (sql_id)
);

alter table sql_info comment 'sql信息表';
