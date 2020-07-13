CREATE TABLE `t_test` (
  `value` varchar(32) DEFAULT NULL COMMENT '班组编号',
  `total` int(11) DEFAULT NULL COMMENT '人员编制（人数）',
  `insert_time` datetime DEFAULT NULL COMMENT '创建时间',
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='测试表';