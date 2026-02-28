-- 商家笼位管理
USE `zxq`;

CREATE TABLE IF NOT EXISTS `shop_cage` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint NOT NULL COMMENT '关联 shop.id',
  `code` varchar(64) NOT NULL COMMENT '笼位编号',
  `size` varchar(16) NOT NULL COMMENT '规格(S/M/L)',
  `price` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '价格(元)',
  `status` varchar(16) NOT NULL DEFAULT 'free' COMMENT '状态: free/occupied',
  `is_delete` tinyint DEFAULT 0 COMMENT '逻辑删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_cage_code` (`shop_id`,`code`),
  KEY `idx_shop_cage_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家笼位表';
