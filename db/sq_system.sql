/*
 Navicat Premium Dump SQL

 Source Server         : home
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : sq_system

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 27/02/2026 21:11:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员 ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '昵称',
  `status` int NOT NULL COMMENT '状态（如 1 启用，0 禁用）',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `is_super_admin` int NOT NULL COMMENT '是否为超级管理员（0 否，1 是）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$vt.IXZKjDeT9uMslthiJveHst44PcnE7SzHj05QOhBeo0jMnfXHga', '普通管理员', 1, NULL, '2026-02-27 18:32:06', '一位普通平台管理员', 0);
INSERT INTO `admin` VALUES (2, 'superAdmin', '$2a$10$vt.IXZKjDeT9uMslthiJveHst44PcnE7SzHj05QOhBeo0jMnfXHga', '超级管理员', 1, NULL, '2026-02-27 18:31:58', '超级管理员', 1);

-- ----------------------------
-- Table structure for admin_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `admin_operation_log`;
CREATE TABLE `admin_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_id` bigint NOT NULL COMMENT '管理员ID',
  `admin_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员账号',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP地址',
  `request_uri` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URI',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户代理',
  `action_module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块',
  `action_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `result_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作结果状态',
  `result_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作结果信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_operation_log
-- ----------------------------
INSERT INTO `admin_operation_log` VALUES (1, 2, 'superAdmin', '127.0.0.1', '/api/auth/admin/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/admin-core,system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiJzdXBlckFkbWluIiwic3RhdHVzIjoxLCJpYXQiOjE3NzIxODgzMzQsImV4cCI6MTc3MjI3NDczNH0.DvskxMHCaIpOaEhduibZd9esWmmtn8Bks1VOqobRGxw', '2026-02-27 18:32:15');
INSERT INTO `admin_operation_log` VALUES (2, 2, 'superAdmin', '127.0.0.1', '/api/auth/admin/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/admin-core,system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiJzdXBlckFkbWluIiwic3RhdHVzIjoxLCJpYXQiOjE3NzIxODg0MjAsImV4cCI6MTc3MjI3NDgyMH0.0Nop2N6jNpTG-HRAbN1eXj0uZig_1sxCKdN8uZCj6lU', '2026-02-27 18:33:41');
INSERT INTO `admin_operation_log` VALUES (3, 2, 'superAdmin', '127.0.0.1', '/api/auth/admin/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/admin-core,system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiJzdXBlckFkbWluIiwic3RhdHVzIjoxLCJpYXQiOjE3NzIxODg2MzMsImV4cCI6MTc3MjI3NTAzM30.WaGWhvp6VC18yQejV3Rqp9NDqsKECFWf4ScdNsjQwKo', '2026-02-27 18:37:13');

-- ----------------------------
-- Table structure for admin_permission
-- ----------------------------
DROP TABLE IF EXISTS `admin_permission`;
CREATE TABLE `admin_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限 ID',
  `perm_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码（如 user:add）',
  `perm_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称（如 添加用户）',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限类型（menu / button / api / data）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权限描述',
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属模块',
  `sort` int NOT NULL COMMENT '排序字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `perm_code`(`perm_code` ASC) USING BTREE,
  INDEX `idx_perm_code`(`perm_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_permission
-- ----------------------------

-- ----------------------------
-- Table structure for admin_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色代码（如 SYS_ADMIN）',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称（如 系统管理员）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role
-- ----------------------------
INSERT INTO `admin_role` VALUES (1, 'super_admin', '超级管理员', '一个账号可以三端互切，前提是注册了用户和商家');
INSERT INTO `admin_role` VALUES (2, 'normal_admin', '普通管理员 ', '一个普通的平台管理员');

-- ----------------------------
-- Table structure for admin_role_to_permission
-- ----------------------------
DROP TABLE IF EXISTS `admin_role_to_permission`;
CREATE TABLE `admin_role_to_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `permission_id` bigint NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色与权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_role_to_permission
-- ----------------------------

-- ----------------------------
-- Table structure for admin_to_project
-- ----------------------------
DROP TABLE IF EXISTS `admin_to_project`;
CREATE TABLE `admin_to_project`  (
  `admin_id` bigint NOT NULL COMMENT '管理员 ID',
  `project_id` bigint NOT NULL COMMENT '项目 ID',
  PRIMARY KEY (`admin_id`, `project_id`) USING BTREE,
  INDEX `idx_project_id`(`project_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员与项目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_to_project
-- ----------------------------
INSERT INTO `admin_to_project` VALUES (1, 1);
INSERT INTO `admin_to_project` VALUES (2, 1);

-- ----------------------------
-- Table structure for admin_to_role
-- ----------------------------
DROP TABLE IF EXISTS `admin_to_role`;
CREATE TABLE `admin_to_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `admin_id` bigint NOT NULL COMMENT '管理员 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_admin_role`(`admin_id` ASC, `role_id` ASC) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin_to_role
-- ----------------------------
INSERT INTO `admin_to_role` VALUES (1, 1, 2);
INSERT INTO `admin_to_role` VALUES (2, 2, 1);

-- ----------------------------
-- Table structure for flood_block_log
-- ----------------------------
DROP TABLE IF EXISTS `flood_block_log`;
CREATE TABLE `flood_block_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `identifier` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求标识符（如 ip:127.0.0.1 / admin:zhangsan）',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求来源 IP 地址',
  `request_uri` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求 URI 路径',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法（GET / POST）',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数 JSON 快照',
  `hit_rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '命中的限流规则（如 每秒10次）',
  `action_taken` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系统对该请求采取的动作（拦截 / 封禁 / 拒绝）',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息（来源模块 / 拦截器名等）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ip_address`(`ip_address` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_identifier`(`identifier` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '洪水攻击拦截日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flood_block_log
-- ----------------------------

-- ----------------------------
-- Table structure for kf_agent
-- ----------------------------
DROP TABLE IF EXISTS `kf_agent`;
CREATE TABLE `kf_agent`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '客服代理 ID',
  `user_id` bigint NOT NULL COMMENT '后台登录账号 ID',
  `dept_id` bigint NOT NULL COMMENT '部门 ID',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `status` int NOT NULL COMMENT '状态（如 1 启用，0 禁用）',
  `head` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_agent_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_agent_dept`(`dept_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服代理表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_agent
-- ----------------------------

-- ----------------------------
-- Table structure for kf_attachment
-- ----------------------------
DROP TABLE IF EXISTS `kf_attachment`;
CREATE TABLE `kf_attachment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `agent_id` bigint NOT NULL COMMENT '客服代理 ID',
  `filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件名',
  `url` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件 URL',
  `mime` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件 MIME 类型',
  `size` bigint NOT NULL COMMENT '文件大小（字节）',
  `kind` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_agent_id`(`agent_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服附件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for kf_conv_read
-- ----------------------------
DROP TABLE IF EXISTS `kf_conv_read`;
CREATE TABLE `kf_conv_read`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `conv_id` bigint NOT NULL COMMENT '会话 ID',
  `side` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '阅读方（如 用户、客服）',
  `last_read_msg_id` bigint NULL DEFAULT NULL COMMENT '最后阅读的消息 ID',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_conv_side`(`conv_id` ASC, `side` ASC) USING BTREE,
  INDEX `idx_conv_id`(`conv_id` ASC) USING BTREE,
  INDEX `idx_side`(`side` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会话阅读记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_conv_read
-- ----------------------------

-- ----------------------------
-- Table structure for kf_conversation
-- ----------------------------
DROP TABLE IF EXISTS `kf_conversation`;
CREATE TABLE `kf_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话 ID',
  `dept_id` bigint NOT NULL COMMENT '部门 ID',
  `agent_id` bigint NOT NULL COMMENT '客服代理 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `last_msg_at` datetime NULL DEFAULT NULL COMMENT '最后消息时间',
  `last_preview` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后预览消息',
  `unread_for_agent` int NOT NULL COMMENT '客服未读消息数',
  `unread_for_user` int NOT NULL COMMENT '用户未读消息数',
  `is_open` tinyint(1) NOT NULL COMMENT '是否开启会话',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_conv`(`dept_id` ASC, `agent_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_conv_agent`(`dept_id` ASC, `agent_id` ASC, `last_msg_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_conversation
-- ----------------------------

-- ----------------------------
-- Table structure for kf_dept
-- ----------------------------
DROP TABLE IF EXISTS `kf_dept`;
CREATE TABLE `kf_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dept_name`(`name` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_dept
-- ----------------------------

-- ----------------------------
-- Table structure for kf_message
-- ----------------------------
DROP TABLE IF EXISTS `kf_message`;
CREATE TABLE `kf_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
  `conv_id` bigint NOT NULL COMMENT '会话 ID',
  `server_msg_id` bigint NULL DEFAULT NULL COMMENT '服务器消息 ID',
  `client_msg_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '客户端消息 ID',
  `from_side` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息发送方（用户 / 客服）',
  `msg_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型',
  `text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文本消息内容',
  `payload_json` json NULL COMMENT '图片/文件元数据（JSON 字符串存）',
  `quote_msg_id` bigint NULL DEFAULT NULL COMMENT '引用的消息 ID',
  `revoked` tinyint(1) NOT NULL COMMENT '消息是否撤回',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_msg_conv_paging`(`conv_id` ASC, `id` ASC) USING BTREE,
  INDEX `idx_msg_client`(`client_msg_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_message
-- ----------------------------

-- ----------------------------
-- Table structure for kf_normal_q
-- ----------------------------
DROP TABLE IF EXISTS `kf_normal_q`;
CREATE TABLE `kf_normal_q`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问题 ID',
  `user_id` int NOT NULL COMMENT '用户 ID',
  `type` int NOT NULL COMMENT '问题类型',
  `status` int NOT NULL COMMENT '问题状态',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '问题内容',
  `result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回答结果',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服常见问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kf_normal_q
-- ----------------------------

-- ----------------------------
-- Table structure for system_project
-- ----------------------------
DROP TABLE IF EXISTS `system_project`;
CREATE TABLE `system_project`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目 ID',
  `project_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目代码',
  `project_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `project_code`(`project_code` ASC) USING BTREE,
  INDEX `idx_project_code`(`project_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_project
-- ----------------------------
INSERT INTO `system_project` VALUES (1, 'zxq', '宠物寄养预约与管理系统', '毕业设计', '2026-02-26 20:46:56');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `status` int NOT NULL DEFAULT 1 COMMENT '用户状态',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `secure_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加密密码',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `alipay` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付宝账号',
  `usdt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'USDT账户',
  `wxpay` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信支付账号',
  `create_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时 IP 地址',
  `now_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '当前 IP 地址',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最近登陆时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, '12312312312', NULL, 1, '2026-02-27 00:14:59', 'qwe123123', NULL, NULL, NULL, '2026-02-27 00:14:59', NULL, NULL, NULL, '127.0.0.1', '127.0.0.1', NULL);
INSERT INTO `user` VALUES (4, '13300000000', NULL, 1, '2026-02-27 17:17:45', 'qwe123123', NULL, NULL, NULL, '2026-02-27 17:17:45', NULL, NULL, NULL, '127.0.0.1', '127.0.0.1', NULL);
INSERT INTO `user` VALUES (5, 'superAdmin', NULL, 1, '2026-02-27 18:39:14', 'qwe123123', NULL, NULL, NULL, '2026-02-27 18:39:22', NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for user_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `user_operation_log`;
CREATE TABLE `user_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `user_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户账号',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP 地址',
  `request_uri` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求 URI 路径',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方法（GET / POST）',
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户代理',
  `action_module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块',
  `action_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `result_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作结果状态',
  `result_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '操作结果信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_operation_log
-- ----------------------------
INSERT INTO `user_operation_log` VALUES (1, 2, '12312312312', '127.0.0.1', '/api/fd/user/register', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'user,system/user-core', '注册用户账号', '12312312312', '成功', '注册成功', '2026-02-27 00:14:59');
INSERT INTO `user_operation_log` VALUES (2, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTIzNDM5LCJleHAiOjE3NzIyMDk4Mzl9.iwBx9IE0iZdShC_7hZDjDC_RXeM6mBqAlxm8IhlRngw', '2026-02-27 00:30:40');
INSERT INTO `user_operation_log` VALUES (3, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTI0NjA4LCJleHAiOjE3NzIyMTEwMDh9.xl4X62GoyvwiS2BOnnVGmDP1KF55JtKPgEa8Q4QYHUY', '2026-02-27 00:50:08');
INSERT INTO `user_operation_log` VALUES (4, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTI0ODM4LCJleHAiOjE3NzIyMTEyMzh9.Zxf_pbyrZsF0RMzN70ax7AJVHI6rxY5Oor1zU-34BLA', '2026-02-27 00:53:58');
INSERT INTO `user_operation_log` VALUES (5, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTU1MDc2LCJleHAiOjE3NzIyNDE0NzZ9._FdbjtYA4ytuVu_B9zqkPVV30Ky6zMwuCZsb6H-0ebA', '2026-02-27 09:17:56');
INSERT INTO `user_operation_log` VALUES (6, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTU1NjE0LCJleHAiOjE3NzIyNDIwMTR9.oaUZBo9smUsuXOVezUnEFuIoveoHSGsPLy6koDCKnCM', '2026-02-27 09:26:55');
INSERT INTO `user_operation_log` VALUES (7, 3, '15714192300', '127.0.0.1', '/api/fd/user/register', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'user,system/user-core', '注册用户账号', '15714192300', '成功', '注册成功', '2026-02-27 09:26:48');
INSERT INTO `user_operation_log` VALUES (8, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTgwMjQ0LCJleHAiOjE3NzIyNjY2NDR9.9lTN8n6jrn9K5KkZ9NwAgUA400gfuOOLgtGBGI7EAeY', '2026-02-27 16:17:25');
INSERT INTO `user_operation_log` VALUES (9, 4, '13300000000', '127.0.0.1', '/api/fd/shop/register', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'shop,system/user-core', '注册用户账号', '13300000000', '成功', '注册成功', '2026-02-27 17:17:45');
INSERT INTO `user_operation_log` VALUES (10, 4, '13300000000', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiIxMzMwMDAwMDAwMCIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTgzODgxLCJleHAiOjE3NzIyNzAyODF9.YGjuvgZ1u5rL-VgU5Cz4ifentmvizghbBQvnfyurCyI', '2026-02-27 17:18:01');
INSERT INTO `user_operation_log` VALUES (11, 4, '13300000000', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6NCwidXNlcm5hbWUiOiIxMzMwMDAwMDAwMCIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTg2NTYzLCJleHAiOjE3NzIyNzI5NjN9.I9l2wRcwOwiH7ZsUVJCv4nncn528b2j68GOWxctgRFU', '2026-02-27 18:02:44');
INSERT INTO `user_operation_log` VALUES (12, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', '用户核心', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTg3MjQ5LCJleHAiOjE3NzIyNzM2NDl9.TBZ-YkI4eJv1qEZrqYDvoNxQMwWTRySj_ex3Hb7JIlo', '2026-02-27 18:14:10');
INSERT INTO `user_operation_log` VALUES (13, 2, '12312312312', '127.0.0.1', '/api/auth/user/login', 'POST', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36 Edg/145.0.0.0', 'system/auth-app', '登录', NULL, '成功', 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MiwidXNlcm5hbWUiOiIxMjMxMjMxMjMxMiIsInN0YXR1cyI6MSwiaWF0IjoxNzcyMTg5NTc2LCJleHAiOjE3NzIyNzU5NzZ9.mk-BdIDd5NEempp_HHcV6zatQNPA22Lu-ThUylg5J2c', '2026-02-27 18:52:57');

-- ----------------------------
-- Table structure for user_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限 ID',
  `perm_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限代码',
  `perm_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限名称',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '权限类型（如 menu / button / api / data）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '权限描述',
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属模块',
  `sort` int NOT NULL COMMENT '排序字段',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `perm_code`(`perm_code` ASC) USING BTREE,
  INDEX `idx_perm_code`(`perm_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_permission
-- ----------------------------
INSERT INTO `user_permission` VALUES (1, 'base_info_update', '个人基础信息修改', 'api', '拥有修改个人基础信息（头像，昵称，性别）', 'user', 1);

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `role_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色代码',
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '角色描述',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code` ASC) USING BTREE,
  INDEX `idx_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 'super_user', '超级用户', '用户有用户的全部权限，仅限开发者使用');
INSERT INTO `user_role` VALUES (2, 'normal_user', '普通用户', '一位非常普通的用户');
INSERT INTO `user_role` VALUES (3, 'noemal_shop', '普通商户', '一个非常普通的商户');

-- ----------------------------
-- Table structure for user_role_to_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_role_to_permission`;
CREATE TABLE `user_role_to_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  `permission_id` bigint NOT NULL COMMENT '权限 ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_permission`(`role_id` ASC, `permission_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_permission_id`(`permission_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色与权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_role_to_permission
-- ----------------------------
INSERT INTO `user_role_to_permission` VALUES (1, 1, 1);

-- ----------------------------
-- Table structure for user_to_project
-- ----------------------------
DROP TABLE IF EXISTS `user_to_project`;
CREATE TABLE `user_to_project`  (
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `project_id` bigint NOT NULL COMMENT '项目 ID',
  PRIMARY KEY (`user_id`, `project_id`) USING BTREE,
  INDEX `idx_project_id`(`project_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与项目关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_to_project
-- ----------------------------
INSERT INTO `user_to_project` VALUES (2, 1);
INSERT INTO `user_to_project` VALUES (4, 1);
INSERT INTO `user_to_project` VALUES (5, 1);

-- ----------------------------
-- Table structure for user_to_role
-- ----------------------------
DROP TABLE IF EXISTS `user_to_role`;
CREATE TABLE `user_to_role`  (
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `role_id` bigint NOT NULL COMMENT '角色 ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户与角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_to_role
-- ----------------------------
INSERT INTO `user_to_role` VALUES (2, 2);
INSERT INTO `user_to_role` VALUES (5, 2);
INSERT INTO `user_to_role` VALUES (4, 3);

SET FOREIGN_KEY_CHECKS = 1;
