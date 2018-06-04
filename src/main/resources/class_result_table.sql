/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : auto_memory_analyze_result_db

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-06-04 10:04:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for class_result_table
-- ----------------------------
DROP TABLE IF EXISTS `class_result_table`;
CREATE TABLE `class_result_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `object_name` text COMMENT '类名',
  `sum_num` double(10,0) unsigned DEFAULT '0' COMMENT '数量',
  `ave_num` double(10,0) unsigned DEFAULT '0' COMMENT '平均数量',
  `max_num` double(10,0) unsigned DEFAULT '0' COMMENT '最大数量',
  `max_num_file_name` text COMMENT '最大数量文件名',
  `sum_retained` double DEFAULT '0' COMMENT '总共引用住的对象大小(m)',
  `ave_retained` double DEFAULT '0' COMMENT '平均引用内存大小',
  `max_retained` double DEFAULT '0' COMMENT '最大引用内存大小(M)',
  `max_retained_file_name` text COMMENT '引用最大内存文件名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=255 DEFAULT CHARSET=utf8;
