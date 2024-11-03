package com.rem.weixin.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Builder;
import lombok.Data;

//自定义标签名：它允许你为类指定一个自定义的 XML 标签名。在序列化（将对象转换为 XML）时，XStream 将使用指定的标签名而不是默认的类名。
//增强可读性：通过自定义标签名，可以使生成的 XML 更加清晰、易于理解，特别是当类名不够直观时。
@XStreamAlias("xml")
@Data
@Builder
public class TextMessage {
    //    接收方账号（收到的OpenID）
    @XStreamAlias("ToUserName")
    private String toUserName;
    //    开发者微信号1
    @XStreamAlias("FromUserName")
    private String fromUserName;
    //    消息创建时间
    @XStreamAlias("CreateTime")
    private long createTime;
    //    消息类型，文本为text
    @XStreamAlias("MsgType")
    private String msgType;
    //    回复的消息内容（换行：在content中能够换行，微信客户端就支持换行显示）
    @XStreamAlias("Content")
    private String content;
    //    事件类型，subscribe SCAN 等
    @XStreamAlias("Event")
    private String event;
    //    事件KEY值，是一个32位无符号整数，即创建二维码时的二维码scene_id
    @XStreamAlias("EventKey")
    private String eventKey;
    //    二维码的ticket，可用来换取二维码图片
    @XStreamAlias("Ticket")
    private String ticket;

}
