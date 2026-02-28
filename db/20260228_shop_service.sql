-- 商家服务管理
USE `zxq`;

CREATE TABLE IF NOT EXISTS `shop_service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT '关联 shop.id',
  `name` varchar(100) NOT NULL COMMENT '服务名称',
  `price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '服务价格',
  `description` varchar(255) DEFAULT NULL COMMENT '服务说明',
  `is_delete` tinyint DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_shop_service_shop_id` (`shop_id`),
  KEY `idx_shop_service_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家服务表';
