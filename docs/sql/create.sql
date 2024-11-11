create database if not exists`pay-mall`;
use `pay-mall`;
drop table if exists pay_order;
CREATE TABLE `pay_order` (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '自增id',
    user_id VARCHAR(32) NOT NULL COMMENT '用户id',
    item_id VARCHAR(32) NOT NULL COMMENT '商品id',
    item_name VARCHAR(64) NOT NULL COMMENT '商品名称',
    item_images VARCHAR(64) COMMENT '商品图片url',
    order_id VARCHAR(32) UNIQUE COMMENT '订单id',
    order_time DATETIME COMMENT '下单时间',
    total_amount DECIMAL(8,2) UNSIGNED DEFAULT NULL COMMENT '订单总金额',
    status VARCHAR(32) NOT NULL COMMENT '订单状态；create-创建完成、pay_wait-等待支付、pay_success-支付成功、deal_done-交易完成、close-订单关单、 refund-退款成功',
    pay_url varchar(2048) DEFAULT NULL COMMENT '支付信息',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    refund_time DATETIME COMMENT '退款时间',
    refund_reason VARCHAR(64) COMMENT '退款原因',
    KEY idx_user_item_id (user_id, item_id)
);

	INSERT INTO `pay_order` (`id`, `user_id`, `item_id`, `item_name`, `order_id`, `order_time`, `total_amount`, `status`, `pay_url`, `pay_time`, `create_time`, `update_time`)
VALUES
 (5,'or0Ab6ivwmypESVp_bYuk92T6SvU','100010090091','测试商品','6809950884118699','2024-08-01 23:04:19',1.68,'PAY_SUCCESS','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=eeZWxln8FzUmqaZyUH0R1gyaOzGlpd8Gv0b7Njl7UZ15PN9BWr6228vFAu3wCY%2Fls7Ne5%2FYQMVXbB3f7nr4TT9v3VvjACmD3d6I3vT0UqtaVjYQcA2KwmH4gfGxAJ1Xcj7L9nd4tBUyUzfh%2BtAk0DQSEfWdjFECanBpZeZloaUFJjyEsO8S73a3ZIET4311Uf5OHdMvSDR%2FqXCoOq7uerlu5yz6lmAAAExus1gTJYbGDPPATpiSjpsTU5ZC7wt%2FtYhmOMmw1vy5wVFqMcQy2IY37lcaOKo%2BInhzY1%2Fo9ZReCM61Ye3kFIhx%2F0RUPfelgFocwgJiDKraHxRmklmm5xw%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fxfg-studio.natapp1.cc%2Fapi%2Fv1%2Falipay%2Fpay_notify&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2024-08-02+07%3A04%3A19&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;6809950884118699&quot;,&quot;total_amount&quot;:&quot;1.68&quot;,&quot;subject&quot;:&quot;测试商品&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2024-08-02 07:08:13','2024-08-02 07:04:19','2024-08-02 07:08:13'),
 (6,'xiaofuge','100010090091','测试商品','9813051810229351','2024-08-04 00:55:29',1.68,'PAY_WAIT','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=b74Ixi48h1BDRVtO64yaA83aFz7t%2FD3TwvcAJ%2BT2orpx8hRbrlKZUNpIauzUTpc3K32i1dCREzbhrA%2BwT11IMojYKevouQ2FLkEMfoCDLQVCyrYp9WvmqcTxrxP8I7bcubYJ%2B46H6z7eHTAqiPQPfSY5LhzrM%2BTIpAakBl1G%2FXXETf2UqfqS%2Ba1jBeYnGBxm8Fuz2%2B7Tx9qMngDkTBM9Q4jInv0O0cxO2jrHZuh0y2ev9gH6x8XFVI0F0Dj6SnLwBFPMVPN6IfOF%2BLyagA641qhN0e%2Bha7uY2cSKqIfsoHbca9wzaxEuf9FsXWNyoDgHmXaWBWsiYqevtsdYMyY7cg%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fxfg-studio.natapp1.cc%2Fapi%2Fv1%2Falipay%2Fpay_notify&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2024-08-04+08%3A55%3A29&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;9813051810229351&quot;,&quot;total_amount&quot;:&quot;1.68&quot;,&quot;subject&quot;:&quot;测试商品&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>',NULL,'2024-08-04 08:55:28','2024-08-04 08:55:29'),
 (7,'or0Ab6ivwmypESVp_bYuk92T6SvU','100010090091','测试商品','3700032384239341','2024-08-04 03:11:19',1.68,'PAY_SUCCESS','<form name=\"punchout_form\" method=\"post\" action=\"https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=ThasDwUrX%2F%2BY6cnC3snirQ9snYZKFTHrryjOuhNQ41Z0Xx1TWnmqYCS86TRF4y%2Fy1ffaItxLqH6iLJhZ4SFttlB9eA01cMe41MjPPtcBoFvdAVTC7%2Fy4GQ7I4JdZd%2FqDF4swwiGy4GmibuFXZ1knyIZnpXbl%2Fy16vWRTWsPw%2BeoWvROoAptRLd6xuLy7FQOUoQUCwCS%2FeLbsu4NNQbuopCGlbBXAxe5XZFn4AHau6nYGlVmefFCMcMtr89HYqA6qC5dSHonXNRwQr1MJI4FYqNDzd60d5eMp5VsoPU%2B78EvN45htiUr1xa%2F2CEvAN1hhpLhH%2BL5y%2BIxU5oXjRFuR4A%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fxfg-studio.natapp1.cc%2Fapi%2Fv1%2Falipay%2Fpay_notify&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2024-08-04+11%3A11%3A19&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json\">\n<input type=\"hidden\" name=\"biz_content\" value=\"{&quot;out_trade_no&quot;:&quot;3700032384239341&quot;,&quot;total_amount&quot;:&quot;1.68&quot;,&quot;subject&quot;:&quot;测试商品&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}\">\n<input type=\"submit\" value=\"立即支付\" style=\"display:none\" >\n</form>\n<script>document.forms[0].submit();</script>','2024-08-10 15:59:19','2024-08-04 11:11:18','2024-08-10 15:59:19');
-- xx