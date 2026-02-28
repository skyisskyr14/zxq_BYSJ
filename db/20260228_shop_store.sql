-- 商家门店配置（评分 + 多图）
USE `zxq`;

CREATE TABLE IF NOT EXISTS `shop_store` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT '关联 shop.id',
  `name` varchar(100) NOT NULL COMMENT '门店名称',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `address` varchar(255) DEFAULT NULL COMMENT '门店地址',
  `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `score` tinyint DEFAULT 5 COMMENT '评分(1-5)',
  `intro` varchar(500) DEFAULT NULL COMMENT '门店介绍',
  `is_delete` tinyint DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_store_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家门店配置表';

CREATE TABLE IF NOT EXISTS `shop_store_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `store_id` bigint NOT NULL COMMENT '关联 shop_store.id',
  `image_url` varchar(500) NOT NULL COMMENT '图片地址',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_shop_store_image_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店图片表';
