/*
 Navicat Premium Dump SQL

 Source Server         : test
 Source Server Type    : MySQL
 Source Server Version : 90300 (9.3.0)
 Source Host           : localhost:3306
 Source Schema         : oasystem

 Target Server Type    : MySQL
 Target Server Version : 90300 (9.3.0)
 File Encoding         : 65001

 Date: 27/03/2026 17:04:38
*/

CREATE DATABASE IF NOT EXISTS `oasystem` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `oasystem`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for doc_info
-- ----------------------------
DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文档标题',
  `file_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始文件名',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存储路径/URL',
  `file_size` bigint NULL DEFAULT 0 COMMENT '文件大小字节',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文档后缀或MIME (pdf/word/excel)',
  `uploader_id` bigint NOT NULL COMMENT '上传者ID',
  `is_share` tinyint NULL DEFAULT 1 COMMENT '共享范围 (0私有只读 1公开)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统公用或私人文档表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of doc_info
-- ----------------------------
INSERT INTO `doc_info` VALUES (1, NULL, '校徽.png', '校徽.png', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\f7ae0881617a4acabae69ed338cf40ab.png', 279085, 'png', 3, 1, '2026-03-27 15:50:35', '2026-03-27 15:50:35');
INSERT INTO `doc_info` VALUES (2, NULL, '事务管理信息.txt', '事务管理信息.txt', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\7a28eb6ec1284171838eaef5e2ef9f72.txt', 157, 'txt', 3, 1, '2026-03-27 15:50:40', '2026-03-27 15:50:40');
INSERT INTO `doc_info` VALUES (3, NULL, '共享信息表格.xlsx', '共享信息表格.xlsx', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\32fc127d423844a985a032c195a7943e.xlsx', 6617, 'xlsx', 3, 1, '2026-03-27 15:50:48', '2026-03-27 15:50:48');
INSERT INTO `doc_info` VALUES (4, NULL, '第一次开会ppt.pptx', '第一次开会ppt.pptx', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\d476780e3a5546d3bcfe958f09d2b12b.pptx', 318625, 'pptx', 3, 1, '2026-03-27 15:54:17', '2026-03-27 15:54:17');
INSERT INTO `doc_info` VALUES (5, NULL, '员工任务.docx', '员工任务.docx', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\789f9fdd681949cfb5370c067acd50be.docx', 14007, 'docx', 3, 1, '2026-03-27 15:54:22', '2026-03-27 15:54:22');
INSERT INTO `doc_info` VALUES (6, NULL, '上传测试1.txt', '上传测试1.txt', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\2f63d68be3bc472bada3b413f4a8b545.txt', 163, 'txt', 1, 1, '2026-03-27 16:36:12', '2026-03-27 16:36:12');
INSERT INTO `doc_info` VALUES (7, NULL, '微信图片_20230603120118.jpg', '微信图片_20230603120118.jpg', 'D:\\Software\\idea\\biyesheji\\OASystem\\backend\\.\\uploads\\docs\\9b4975706fb142bab32b2bd975f3a5fc.jpg', 70727, 'jpg', 6, 1, '2026-03-27 16:41:12', '2026-03-27 16:41:12');

-- ----------------------------
-- Table structure for notice_info
-- ----------------------------
DROP TABLE IF EXISTS `notice_info`;
CREATE TABLE `notice_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '内容',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型 (1全员通告 2部门通知)',
  `publisher_id` bigint NOT NULL COMMENT '发布人',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 (0草稿 1已发布)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '全系统公告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice_info
-- ----------------------------
INSERT INTO `notice_info` VALUES (1, '全体员工放假公告', '正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期正常过五一假期', 1, 1, 1, '2026-03-27 16:00:46', '2026-03-27 16:01:32');
INSERT INTO `notice_info` VALUES (2, '测试公告', '测试公告已发布', 1, 1, 1, '2026-03-27 16:37:44', '2026-03-27 16:39:11');

-- ----------------------------
-- Table structure for sys_meeting
-- ----------------------------
DROP TABLE IF EXISTS `sys_meeting`;
CREATE TABLE `sys_meeting`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会议主题',
  `room` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会议室',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `participants` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参与人员(名称或ID序列)',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会议日程大屏管理调度底层库' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_meeting
-- ----------------------------
INSERT INTO `sys_meeting` VALUES (1, '第一次公司会议', 'A座101科技阶梯室', '2026-03-27 16:00:00', '2026-03-27 18:00:00', '全部公司成员', '2026-03-27 15:55:45');
INSERT INTO `sys_meeting` VALUES (2, '高层领导指导会议', 'B座203执行总裁室', '2026-03-27 10:00:00', '2026-03-27 11:59:17', '主管及以上干部', '2026-03-27 16:00:11');
INSERT INTO `sys_meeting` VALUES (3, '与李四代码审查建议会议', 'A座101科技阶梯室', '2026-03-28 09:00:00', '2026-03-28 10:00:00', '与李四代码审查建议会议', '2026-03-27 16:12:08');
INSERT INTO `sys_meeting` VALUES (4, '公司高管会议', 'B座203执行总裁室', '2026-03-27 08:00:00', '2026-03-27 10:00:00', '全部高管', '2026-03-27 16:37:21');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别 (0未知 1男 2女)',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '所属部门ID',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态 (0停用 1正常)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'staff' COMMENT '角色: admin=超级管理员, manager=部门主管, staff=普通员工',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_dept_id`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$1y.S2qb.kMPn9k/30Nqx1eODxaLi2Oh2RT6Ujdi5SKBXESM2pwSIi', '超级管理员', '/uploads/avatars/a764de5f952e4a2a99538fa00dc690e7.jpg', 'admin@oasystem.com', '13800000098', 0, 1, 1, '2026-03-20 14:16:38', '2026-03-27 16:34:23', 'admin');
INSERT INTO `sys_user` VALUES (2, 'manager', '$2a$10$1y.S2qb.kMPn9k/30Nqx1eODxaLi2Oh2RT6Ujdi5SKBXESM2pwSIi', '研发主管', '/uploads/avatars/5136c1fa34e941c08add60c29da40275.jpg', 'manager@oasystem.com', '13800000001', 0, 2, 1, '2026-03-20 14:16:38', '2026-03-20 17:55:39', 'manager');
INSERT INTO `sys_user` VALUES (3, 'user', '$2a$10$1y.S2qb.kMPn9k/30Nqx1eODxaLi2Oh2RT6Ujdi5SKBXESM2pwSIi', '张三员工', NULL, 'user@oasystem.com', '13800000002', 0, 2, 1, '2026-03-20 14:16:38', '2026-03-20 14:16:38', 'staff');
INSERT INTO `sys_user` VALUES (6, 'lisi', '$2a$10$KduZnpeqbuK7CTuroCY94OeJzMSqsWu8pliY2L71UZixgKTio8Vdi', '李四员工', NULL, '13800000005@163.com', '13800000006', 0, NULL, 1, '2026-03-27 15:44:18', '2026-03-27 16:13:29', 'staff');
INSERT INTO `sys_user` VALUES (7, 'wangwu', '$2a$10$jht1nRUI1YJk8G19lOMFRuazqfRJhbKOscUlgbS0o7P5efk9lFk2y', '王五财务', NULL, '15100000001@gmail.com', '15100000001', 0, NULL, 1, '2026-03-27 15:56:51', '2026-03-27 15:56:51', 'staff');
INSERT INTO `sys_user` VALUES (8, 'zhaoliu', '$2a$10$jAGT.58NlFHNZ3yct.pTL.tV3PFdhrahneSW1CW3VXCtqbPcgBDCW', '赵六财务', NULL, '16600000001@163.com', '16600000001', 0, NULL, 0, '2026-03-27 16:38:17', '2026-03-27 16:38:17', 'staff');

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务详情',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `assignee_id` bigint NOT NULL COMMENT '处理人ID/指派给谁',
  `deadline` datetime NULL DEFAULT NULL COMMENT '截止时间',
  `priority` tinyint NULL DEFAULT 2 COMMENT '优先级 (1低 2中 3高)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态 (0待办 1进行中 2已完成 3已逾期)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '协同任务信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of task_info
-- ----------------------------
INSERT INTO `task_info` VALUES (1, '与李四完成前端代码审查', '与李四完成前端代码审查', 3, 6, NULL, 2, 2, '2026-03-27 15:45:58', '2026-03-27 16:12:30');
INSERT INTO `task_info` VALUES (2, '崔处理', '催处理任务：主管完成请假审批', 1, 2, NULL, 2, 2, '2026-03-27 15:59:01', '2026-03-27 16:41:52');
INSERT INTO `task_info` VALUES (3, '等待主管验收', '完成测试等待主管验收', 6, 2, NULL, 2, 2, '2026-03-27 16:12:56', '2026-03-27 16:41:53');
INSERT INTO `task_info` VALUES (4, '与主管的协同任务审查', '与主管的协同任务审查', 1, 2, NULL, 2, 2, '2026-03-27 16:35:47', '2026-03-27 16:41:54');

-- ----------------------------
-- Table structure for wf_form_leave
-- ----------------------------
DROP TABLE IF EXISTS `wf_form_leave`;
CREATE TABLE `wf_form_leave`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '表单ID',
  `instance_id` bigint NULL DEFAULT NULL COMMENT '流程实例ID',
  `user_id` bigint NOT NULL COMMENT '申请人',
  `leave_type` tinyint NOT NULL COMMENT '请假类型 (1年假 2事假 3病假 4调休)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请假事由',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '请假申请业务数据单' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wf_form_leave
-- ----------------------------
INSERT INTO `wf_form_leave` VALUES (1, 1, 3, 2, '2026-03-11 00:00:00', '2026-03-13 00:00:00', '本人因生病需要请假两天，望批准。', '2026-03-27 15:45:10');
INSERT INTO `wf_form_leave` VALUES (2, 2, 1, 1, '2025-11-06 00:00:00', '2026-11-13 00:00:00', '测试系统', '2026-03-27 15:57:52');
INSERT INTO `wf_form_leave` VALUES (4, 4, 2, 4, '2026-03-26 00:00:00', '2026-03-27 00:00:00', '申请调休一天', '2026-03-27 16:10:27');
INSERT INTO `wf_form_leave` VALUES (5, 5, 6, 2, '2026-03-28 00:00:00', '2026-03-29 00:00:00', '因有事需要请假一天，望批准。', '2026-03-27 16:13:54');
INSERT INTO `wf_form_leave` VALUES (6, 6, 1, 2, '2026-03-30 00:00:00', '2026-03-31 00:00:00', '本人因有事需要请假一天，望批准。', '2026-03-27 16:34:53');
INSERT INTO `wf_form_leave` VALUES (7, 7, 7, 3, '2026-03-26 00:00:00', '2026-03-28 00:00:00', '因伤病需要请假两天。', '2026-03-27 16:39:49');

-- ----------------------------
-- Table structure for wf_instance
-- ----------------------------
DROP TABLE IF EXISTS `wf_instance`;
CREATE TABLE `wf_instance`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流程实例ID',
  `template_id` bigint NOT NULL COMMENT '对应模板ID',
  `apply_user_id` bigint NOT NULL COMMENT '申请人ID',
  `apply_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '实例状态 (0审批中 1已通过 2已驳回 3已撤销)',
  `business_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '对应业务表单记录的ID或编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户申请发起的流程实例表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wf_instance
-- ----------------------------
INSERT INTO `wf_instance` VALUES (1, 1, 3, '2026-03-27 15:45:10', 1, '1');
INSERT INTO `wf_instance` VALUES (2, 1, 1, '2026-03-27 15:57:52', 1, '2');
INSERT INTO `wf_instance` VALUES (3, 1, 2, '2026-03-27 16:07:49', 1, '3');
INSERT INTO `wf_instance` VALUES (4, 1, 2, '2026-03-27 16:10:27', 1, '4');
INSERT INTO `wf_instance` VALUES (5, 1, 6, '2026-03-27 16:13:54', 1, '5');
INSERT INTO `wf_instance` VALUES (6, 1, 1, '2026-03-27 16:34:53', 2, '6');
INSERT INTO `wf_instance` VALUES (7, 1, 7, '2026-03-27 16:39:49', 1, '7');

-- ----------------------------
-- Table structure for wf_instance_node
-- ----------------------------
DROP TABLE IF EXISTS `wf_instance_node`;
CREATE TABLE `wf_instance_node`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '流转节点ID',
  `instance_id` bigint NOT NULL COMMENT '实例ID',
  `node_id` bigint NOT NULL COMMENT '对应的模板节点ID',
  `approver_id` bigint NULL DEFAULT NULL COMMENT '实际或指派的审批人ID',
  `action` tinyint NULL DEFAULT 0 COMMENT '审批操作 (0待处理 1同意 2驳回)',
  `comment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审批意见',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '流转状态 (0未到该节点 1当前待审批节点 2已流转)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '审批实例实际扭转记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of wf_instance_node
-- ----------------------------
INSERT INTO `wf_instance_node` VALUES (1, 1, 1, 2, 1, NULL, '2026-03-27 16:02:21', 2);
INSERT INTO `wf_instance_node` VALUES (2, 2, 1, 2, 1, NULL, '2026-03-27 16:10:16', 2);
INSERT INTO `wf_instance_node` VALUES (3, 3, 1, 2, 1, NULL, '2026-03-27 16:08:49', 2);
INSERT INTO `wf_instance_node` VALUES (4, 4, 1, 2, 1, NULL, '2026-03-27 16:10:30', 2);
INSERT INTO `wf_instance_node` VALUES (5, 5, 1, 2, 1, NULL, '2026-03-27 16:40:22', 2);
INSERT INTO `wf_instance_node` VALUES (6, 6, 1, 2, 2, NULL, '2026-03-27 16:40:26', 2);
INSERT INTO `wf_instance_node` VALUES (7, 7, 1, 2, 1, NULL, '2026-03-27 16:40:28', 2);

SET FOREIGN_KEY_CHECKS = 1;
