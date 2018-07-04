/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : auto_memory_analyze_result_db

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-07-04 21:25:05
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `activity_result_table`
-- ----------------------------
DROP TABLE IF EXISTS `activity_result_table`;
CREATE TABLE `activity_result_table` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id值',
  `object_name` text,
  `object_address_id` text COMMENT '对象的内存地址id',
  `sum_num` int(10) unsigned DEFAULT '0' COMMENT '数量',
  `sum_leak` double unsigned DEFAULT '0' COMMENT '总共泄漏的内存(M)',
  `ave_leak` double unsigned DEFAULT '0' COMMENT '平均每次泄漏内存(M)',
  `max_leak` double DEFAULT '0' COMMENT '最大泄漏内存',
  `max_leak_file_name` text COMMENT '最大泄漏内存的文件名',
  `gc_root` text COMMENT '泄漏对象的gc路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=387 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of activity_result_table
-- ----------------------------
INSERT INTO `activity_result_table` VALUES ('385', 'com.tencent.qgame.presentation.activity.MainActivity', '0x13bfebb0', '6', '0.00931549072265625', '0.001552581787109375', '0.001552581787109375', 'test2.hprof', 'GC ROOT thread android.os.HandlerThread.<Java Local> (named \'DFM Handler Thread #0\')\n |- master.flame.danmaku.controller.DrawHandler.mDanmakuView\n  |- master.flame.danmaku.ui.widget.DanmakuView.mContext\n   |- com.tencent.qgame.presentation.activity.MainActivity instance\n');
INSERT INTO `activity_result_table` VALUES ('386', 'com.tencent.qgame.presentation.activity.VideoRoomActivity', '0x138a3660', '5', '0.0063323974609375', '0.0012664794921875', '0.0012664794921875', 'test2.hprof', 'GC ROOT thread java.util.TimerThread.<Java Local> (named \'Timer-13\')\n |- com.tencent.qgame.presentation.widget.video.controller.VideoBufferingView$3.this$0 (anonymous subclass of java.util.TimerTask)\n  |- com.tencent.qgame.presentation.widget.video.controller.VideoBufferingView.mContext\n   |- com.tencent.qgame.presentation.activity.VideoRoomActivity instance\n');
