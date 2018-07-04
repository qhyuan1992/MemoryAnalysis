/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : auto_memory_analyze_result_db

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-07-04 21:25:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `class_result_table`
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

-- ----------------------------
-- Records of class_result_table
-- ----------------------------
INSERT INTO `class_result_table` VALUES ('205', 'char[]', '828328', '0', '103541', 'test1.hprof', '50.75482177734375', '0', '6.344352722167969', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('206', 'java.lang.String', '782072', '0', '97759', 'test1.hprof', '44.0804443359375', '0', '5.5100555419921875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('207', 'int[]', '183968', '0', '22996', 'test1.hprof', '11.615814208984375', '0', '1.4519767761230469', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('208', 'java.lang.Object[]', '136936', '0', '17117', 'test1.hprof', '71.44327545166016', '0', '8.93040943145752', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('209', 'byte[]', '107664', '0', '13458', 'test1.hprof', '372.19322204589844', '0', '46.524152755737305', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('210', 'float[]', '57472', '0', '7184', 'test1.hprof', '1.037628173828125', '0', '0.12970352172851562', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('211', 'java.lang.ref.FinalizerReference', '55880', '0', '6985', 'test1.hprof', '6693.605804443359', '0', '836.7007255554199', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('212', 'java.util.ArrayList', '43616', '0', '5452', 'test1.hprof', '13.968948364257812', '0', '1.7461185455322266', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('213', 'java.lang.Integer', '33280', '0', '4160', 'test1.hprof', '0.380859375', '0', '0.047607421875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('214', 'android.graphics.Rect', '32464', '0', '4058', 'test1.hprof', '0.7430419921875', '0', '0.0928802490234375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('215', 'sun.misc.Cleaner', '30104', '0', '3763', 'test1.hprof', '1.844329833984375', '0', '0.23054122924804688', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('216', 'android.support.v4.util.ArrayMap', '29248', '0', '3656', 'test1.hprof', '1.731475830078125', '0', '0.21643447875976562', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('217', 'java.lang.ref.WeakReference', '23600', '0', '2950', 'test1.hprof', '0.5401611328125', '0', '0.0675201416015625', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('218', 'java.util.HashMap', '21720', '0', '2715', 'test1.hprof', '11.222343444824219', '0', '1.4027929306030273', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('219', 'java.lang.String[]', '21344', '0', '2668', 'test1.hprof', '1.085418701171875', '0', '0.13567733764648438', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('220', 'long[]', '18112', '0', '2264', 'test1.hprof', '1.1319580078125', '0', '0.1414947509765625', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('221', 'android.view.RenderNode', '16152', '0', '2019', 'test1.hprof', '0.36968994140625', '0', '0.04621124267578125', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('222', 'com.color.screenshot.ColorLongshotViewController', '15024', '0', '1878', 'test1.hprof', '0.372528076171875', '0', '0.046566009521484375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('223', 'java.lang.StringBuilder', '14976', '0', '1872', 'test1.hprof', '3.0020751953125', '0', '0.3752593994140625', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('224', 'android.graphics.Paint', '14256', '0', '1782', 'test1.hprof', '1.1270904541015625', '0', '0.1408863067626953', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('225', 'com.taobao.weex.dom.flex.Spacing', '12456', '0', '1557', 'test1.hprof', '0.5708084106445312', '0', '0.0713510513305664', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('226', 'android.graphics.RectF', '9792', '0', '1224', 'test1.hprof', '0.22412109375', '0', '0.02801513671875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('227', 'java.lang.Object', '9176', '0', '1147', 'test1.hprof', '0.07000732421875', '0', '0.00875091552734375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('228', 'android.view.View[]', '9152', '0', '1144', 'test1.hprof', '20.46979522705078', '0', '2.5587244033813477', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('229', 'java.util.HashSet', '8656', '0', '1082', 'test1.hprof', '1.4041748046875', '0', '0.1755218505859375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('230', 'android.util.SparseArray', '8560', '0', '1070', 'test1.hprof', '11.051765441894531', '0', '1.3814706802368164', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('231', 'android.util.LongSparseLongArray', '7672', '0', '959', 'test1.hprof', '0.476654052734375', '0', '0.059581756591796875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('232', 'java.util.concurrent.atomic.AtomicBoolean', '7648', '0', '956', 'test1.hprof', '0.0875244140625', '0', '0.0109405517578125', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('233', 'com.taobao.weex.dom.WXStyle', '7304', '0', '913', 'test1.hprof', '1.416412353515625', '0', '0.17705154418945312', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('234', 'com.taobao.weex.dom.flex.CSSStyle', '7096', '0', '887', 'test1.hprof', '0.6853256225585938', '0', '0.08566570281982422', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('235', 'com.taobao.weex.dom.WXAttr', '7088', '0', '886', 'test1.hprof', '0.50946044921875', '0', '0.06368255615234375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('236', 'android.util.ArrayMap', '6904', '0', '863', 'test1.hprof', '0.48003387451171875', '0', '0.060004234313964844', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('237', 'java.io.File', '6776', '0', '847', 'test1.hprof', '1.0627593994140625', '0', '0.1328449249267578', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('238', 'java.lang.Throwable', '6216', '0', '777', 'test1.hprof', '2.203643798828125', '0', '0.2754554748535156', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('239', 'android.databinding.ObservableField', '6120', '0', '765', 'test1.hprof', '0.4677734375', '0', '0.0584716796875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('240', 'com.facebook.drawee.drawable.DrawableProperties', '6000', '0', '750', 'test1.hprof', '0.1430511474609375', '0', '0.017881393432617188', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('241', 'java.lang.Long', '5800', '0', '725', 'test1.hprof', '0.0885009765625', '0', '0.0110626220703125', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('242', 'rx.internal.util.SubscriptionList', '5256', '0', '657', 'test1.hprof', '0.25774383544921875', '0', '0.032217979431152344', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('243', 'android.databinding.PropertyChangeRegistry', '5064', '0', '633', 'test1.hprof', '0.469451904296875', '0', '0.058681488037109375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('244', 'java.lang.StackTraceElement', '4984', '0', '623', 'test1.hprof', '0.12689208984375', '0', '0.01586151123046875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('245', 'com.facebook.common.references.SharedReference', '4824', '0', '603', 'test1.hprof', '0.099090576171875', '0', '0.012386322021484375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('246', 'com.taobao.weex.dom.WXDomObject', '4576', '0', '572', 'test1.hprof', '2.9398422241210938', '0', '0.3674802780151367', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('247', 'com.alibaba.fastjson.serializer.SerialContext', '4344', '0', '543', 'test1.hprof', '0.09942626953125', '0', '0.01242828369140625', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('248', 'org.aspectj.runtime.reflect.SourceLocationImpl', '4056', '0', '507', 'test1.hprof', '0.077362060546875', '0', '0.009670257568359375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('249', 'com.tencent.qgame.data.model.gift.GiftInfo', '4040', '0', '505', 'test1.hprof', '1.3093643188476562', '0', '0.16367053985595703', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('250', 'org.aspectj.runtime.reflect.CatchClauseSignatureImpl', '3968', '0', '496', 'test1.hprof', '0.16650390625', '0', '0.02081298828125', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('251', 'boolean[]', '3952', '0', '494', 'test1.hprof', '0.0667266845703125', '0', '0.008340835571289062', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('252', 'java.util.concurrent.locks.ReentrantLock', '3896', '0', '487', 'test1.hprof', '0.047698974609375', '0', '0.005962371826171875', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('253', 'android.util.Range', '3784', '0', '473', 'test1.hprof', '0.11492919921875', '0', '0.01436614990234375', 'test1.hprof');
INSERT INTO `class_result_table` VALUES ('254', 'com.tencent.smtt.devtools.main.b', '0', '0', '0', 'test1.hprof', '0', '0', '0', 'test1.hprof');
