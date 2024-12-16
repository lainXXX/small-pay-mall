/*
 Navicat Premium Data Transfer

 Source Server         : 华为云
 Source Server Type    : MySQL
 Source Server Version : 90100
 Source Host           : 110.41.180.185:3306
 Source Schema         : pay-mall

 Target Server Type    : MySQL
 Target Server Version : 90100
 File Encoding         : 65001

 Date: 16/12/2024 13:23:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for item
-- ----------------------------
DROP TABLE IF EXISTS `item`;
CREATE TABLE `item`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `item_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `item_name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `item_image` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品图片url',
  `item_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品描述',
  `item_quantity` int NOT NULL DEFAULT 999 COMMENT '商品库存',
  `amount` decimal(8, 2) NOT NULL DEFAULT 299.00 COMMENT '商品价格',
  `item_status` int NOT NULL DEFAULT 1 COMMENT '商品状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_item_id`(`item_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of item
-- ----------------------------
INSERT INTO `item` VALUES (1, '100001', 'chiikawa home', 'http://javarem.top/images/chiikawa1.jpeg', NULL, 0, 999.00, 1, '2024-12-16 12:59:13', '2024-12-16 13:17:03');
INSERT INTO `item` VALUES (2, '100002', '2', 'http://javarem.top/images/chiikawa2.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:03:54', '2024-12-16 13:03:54');
INSERT INTO `item` VALUES (3, '100003', '3', 'http://javarem.top/images/chiikawa3.jpg', NULL, 999, 19.00, 1, '2024-12-16 13:04:40', '2024-12-16 13:21:16');
INSERT INTO `item` VALUES (4, '100004', '4', 'http://javarem.top/images/chiikawa4.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:05:34', '2024-12-16 13:05:34');
INSERT INTO `item` VALUES (5, '100005', '5', 'http://javarem.top/images/chiikawa5.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:05:50', '2024-12-16 13:20:00');
INSERT INTO `item` VALUES (6, '100006', '6', 'http://javarem.top/images/img1.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:06:16', '2024-12-16 13:07:00');
INSERT INTO `item` VALUES (7, '100007', '7', 'http://javarem.top/images/img2.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:06:29', '2024-12-16 13:20:22');
INSERT INTO `item` VALUES (8, '100008', '8', 'http://javarem.top/images/img3.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:06:54', '2024-12-16 13:07:05');
INSERT INTO `item` VALUES (9, '100009', '', 'http://javarem.top/images/img4.jpeg', NULL, 999, 299.00, 1, '2024-12-16 13:07:25', '2024-12-16 13:07:25');

-- ----------------------------
-- Table structure for pay_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `user_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户id',
  `item_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品id',
  `item_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `item_image` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片url',
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单id',
  `order_time` datetime NULL DEFAULT NULL COMMENT '下单时间',
  `total_amount` decimal(8, 2) UNSIGNED NULL DEFAULT NULL COMMENT '订单总金额',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单、 refund-退款成功',
  `pay_url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付信息',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `refund_time` datetime NULL DEFAULT NULL COMMENT '退款时间',
  `refund_reason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '退款原因',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_user_item_id`(`user_id` ASC, `item_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pay_order
-- ----------------------------
INSERT INTO `pay_order` VALUES (8, 'oElx26a5vWVLG_-AZ_e4ilgw8yK0', '100001', 'chiikawa home', 'http://javarem.top/images/chiikawa1.jpeg', '1734326195297487', '2024-12-16 13:16:36', 999.00, 'DEAL_DONE', '<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=ZFcdkQ1DRX7DSSKj4PBd%2FTYwJmGwSrkKU3wBSlEjLIG9CJck1yJnkzEBbqmuosvi%2BPj9FfAeU0Pwd3EOiymlM2c8bn%2FxA89mhY42x%2BMCSnephP%2FiCxHFh%2BLAEJncnouxLUkSS9Veu%2BBFGIYcr8xoKsBmVi4YQKJKVndmQQ5e42hV3Ms9mF%2Be%2BCDo8J1GBkX0pTd%2FtjcMSZlkuIub%2Bu5WHmbHekzLt6edXmxQ%2Bx566OyIzhnO5zHJOtj%2Byh6W85Hb7Qm8VxOtjR6Ym4RgIVRirHpDr4QP0kBK8WRmEZ%2BdSmP9B%2FiHovOkIHG90XGlLKbHPcajYwAKPdK%2Fd%2B%2BwrJgcVQ%3D%3D&return_url=http%3A%2F%2Fwww.javarem.top%2ForderManager.html&notify_url=http%3A%2F%2F110.41.180.185%3A8090%2Fpay%2Fnotify&version=1.0&app_id=9021000141645053&sign_type=RSA2&timestamp=2024-12-16+13%3A16%3A35&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;1734326195297487&quot;,&quot;total_amount&quot;:&quot;999&quot;,&quot;subject&quot;:&quot;chiikawa home&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>', '2024-12-16 13:17:03', '2024-12-16 13:16:35', '2024-12-16 13:17:34', NULL, NULL);
INSERT INTO `pay_order` VALUES (9, 'oElx26a5vWVLG_-AZ_e4ilgw8yK0', '100001', 'chiikawa home', 'http://javarem.top/images/chiikawa1.jpeg', '1734326258144756', '2024-12-16 13:17:38', 999.00, 'PAY_WAIT', '<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=foKT%2FvN3HpcYq7PySHd88%2BAhorl7Mine3gJfyG77KaDK5jJORjTIfI3SGyUZKmbyHPN8IZm2i9%2FdY3N5iVLrHI3Mezz8Abmh5XNOnsRQOqrkMzR0sb9Qg7HQjETLscvEcVaXdYNU3H5dbsgA0YCc6BpQwkW2IuKEKubdXGFG145ZrVwfKG%2BGB9tKRq95ngvxGP%2FX%2F3Gcr40O%2BKtH32uUM7AJSNjCrLHgqJd9W2c%2F4mkZdEKPGNxucyV8ZXPyTAWtxMNeM2T53wepsfq2q3OCAPv2TlLLu1KsGsN4Vbe2SVz83OTuUf2zfargqhsDOVTZ5dS09uiNSzTBatmV5boeqQ%3D%3D&return_url=http%3A%2F%2Fwww.javarem.top%2ForderManager.html&notify_url=http%3A%2F%2F110.41.180.185%3A8090%2Fpay%2Fnotify&version=1.0&app_id=9021000141645053&sign_type=RSA2&timestamp=2024-12-16+13%3A17%3A38&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;1734326258144756&quot;,&quot;total_amount&quot;:&quot;999&quot;,&quot;subject&quot;:&quot;chiikawa home&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>', NULL, '2024-12-16 13:17:38', '2024-12-16 13:17:38', NULL, NULL);
INSERT INTO `pay_order` VALUES (10, 'oElx26a5vWVLG_-AZ_e4ilgw8yK0', '100001', 'chiikawa home', 'http://javarem.top/images/chiikawa1.jpeg', '1734326272775047', '2024-12-16 13:17:53', 999.00, 'PAY_WAIT', '<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=hvH9OTGApYigvku85A3WQ1rJDQvTsSIgRxqN51HVDvlfEjGaTAm1rhQsC9cyXgnbVbHIiRHTtntLr2jOoVlXoiwqbQg6eXvL82u6%2FQ8TAOvpJwHKjAGuZ%2BwYKCdlxkPf1KGnKaoZOmvjQ0CFU8%2FgBU%2B9%2B30h0SCs%2BEPEdfXjHiXpiScNmpdkS0q8qevfz7e5hLRtxY6acvD7dzDuQhX9rSWbiSmbMHwe5RtxiexV7u1QKTSRTbtSK2LC%2FA2PWAYU46D1QqddLTptHDAMaIAWYy0DZGWBIpx6l3RSBfWJ%2FS05PW4S1zctwW%2B1cMJK0tomqYFARSKLBc0ngb1AI3CDIA%3D%3D&return_url=http%3A%2F%2Fwww.javarem.top%2ForderManager.html&notify_url=http%3A%2F%2F110.41.180.185%3A8090%2Fpay%2Fnotify&version=1.0&app_id=9021000141645053&sign_type=RSA2&timestamp=2024-12-16+13%3A17%3A52&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;1734326272775047&quot;,&quot;total_amount&quot;:&quot;999&quot;,&quot;subject&quot;:&quot;chiikawa home&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>', NULL, '2024-12-16 13:17:53', '2024-12-16 13:17:53', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
