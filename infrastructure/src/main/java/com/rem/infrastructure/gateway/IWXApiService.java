package com.rem.infrastructure.gateway;


import com.rem.infrastructure.gateway.dto.AccessTokenRes;
import com.rem.infrastructure.gateway.dto.WXQrCodeReq;
import com.rem.infrastructure.gateway.dto.WXQrCodeRes;
import com.rem.types.sdk.weixin.message.WeixinTemplateMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface IWXApiService {


    /**
     * 获取AccessToken
     * @param grantType 获取access_token填写client_credential
     * @param appId 第三方用户唯一凭证
     * @param appSecret 第三方用户唯一凭证密钥，即appsecret
     * @return 响应结果
     */
    @GET("/cgi-bin/token")
    Call<AccessTokenRes> getAccessToken(@Query("grant_type") String grantType,
                                        @Query("appid") String appId,
                                        @Query("secret") String appSecret);

    /**
     *
     * @param accessToken getAccessToken 获取的 token 信息
     * @param wxQrCodeReq 入参对象
     * @return 应答结果
     */
    @POST("/cgi-bin/qrcode/create")
    Call<WXQrCodeRes> getQrCode(@Query("access_token") String accessToken,
                                @Body WXQrCodeReq wxQrCodeReq);


    /**
     * 发送微信公众号模板消息
     * 文档：https://mp.weixin.qq.com/debug/cgi-bin/readtmpl?t=tmplmsg/faq_tmpl
     *
     * @param accessToken getToken 获取的 token 信息
     * @param weixinTemplateMessage 入参对象
     * @return
     */
    @POST("cgi-bin/message/template/send")
    Call<Void> sendMessage(@Query("access_token") String accessToken, @Body WeixinTemplateMessage weixinTemplateMessage);

}
