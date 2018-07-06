/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : auto_memory_analyze_result_db

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-07-06 15:11:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `handle_result_table`
-- ----------------------------
DROP TABLE IF EXISTS `handle_result_table`;
CREATE TABLE `handle_result_table` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `handle_file_name` text COMMENT '处理过的hprof文件名',
  `handle_type` int(11) DEFAULT '0' COMMENT '处理的类型\r\n1：instance和activity处理\r\n2：class处理',
  `status` int(10) unsigned DEFAULT '0' COMMENT '处理的状态：\r\n0：默认，未被成功解析\r\n1：被成功解析',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of handle_result_table
-- ----------------------------
