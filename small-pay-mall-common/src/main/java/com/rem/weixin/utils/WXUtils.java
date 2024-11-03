package com.rem.weixin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rem.weixin.message.TextMessage;
import com.rem.weixin.properties.AccessToken;
import com.rem.weixin.properties.WXProperties;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class WXUtils {
    /**
     * 签名验证
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public static boolean signatureCheck(String signature, String timestamp, String nonce, String echostr, String token) {
//        1.将token、timestamp、nonce三个参数进行字典序排序
        List<String> list = Arrays.asList(token, timestamp, nonce);
        Collections.sort(list);
//        用于创建可变字符串的类
        StringBuilder stringBuilder = new StringBuilder();
//        将三个参数字符串拼接成一个字符串
        for (String s : list) {
            stringBuilder.append(s);
        }
//        2）进行sha1加密
        String hexString = null;
        try {
            //使用SHA-1加密
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            byte[] encodedhash = instance.digest(stringBuilder.toString().getBytes());
            StringBuilder builder = new StringBuilder();
//            将字节转换为十六进制字符串
            for (byte b : encodedhash) {
                builder.append(Integer.toHexString((b >> 4) & 15));
                builder.append(Integer.toHexString(b & 15));
            }
            hexString = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }//            3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        return hexString != null && hexString.equals(signature);
    }

    /**
     * 解析XML
     *
     * @param request
     * @return
     * @throws IOException
     */
    public static Map<String, String> XMLToMap(HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        HashMap<String, String> map = new HashMap<>();
        SAXReader saxReader = new SAXReader();
        try {
//            读取request输入流 获取Document对象
            Document document = saxReader.read(inputStream);
//            获取root节点
            Element root = document.getRootElement();
//            获取全部子节点
            List<Element> elements = root.elements();
            for (Element element : elements) {
                map.put(element.getName(), element.getStringValue());
            }
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
//        释放资源
        inputStream.close();
        return map;
    }

    /**
     * 获取返回微信客户端的回复消息
     *
     * @param map map集合
     * @return XML字符串
     */
    public static String mapToXML(Map<String, String> map) {
        TextMessage message = TextMessage.builder()
                .toUserName(map.get("FromUserName"))
                .fromUserName(map.get("ToUserName"))
                .createTime(System.currentTimeMillis() / 1000)
                .msgType("text")
                .content("你好我喜欢你")
                .build();
//        XStream将java对象转成xml字符串
        XStream xStream = new XStream();
        xStream.processAnnotations(TextMessage.class);
        String xml = xStream.toXML(message);
        return xml;
    }

    /**
     * bean转成微信的XML消息格式
     */
    public static String beanToXML(Object object) {
        XStream xStream = new XStream();
//        为这个类设置别名 也就是指定根元素为<xml> </xml>
        xStream.alias("xml", object.getClass());
        xStream.processAnnotations(object.getClass());
        String xml = xStream.toXML(object);
        if (!StringUtils.isEmpty(xml)) {
            return xml;
        } else {
            return null;
        }
    }

    /**
     * XML转成bean泛型方法
     */
    public static <T> T XMLToBean(String resultXml, Class clazz) {
        // XStream对象设置默认安全防护，同时设置允许的类
        XStream stream = new XStream(new DomDriver());
        stream.addPermission(AnyTypePermission.ANY);
        XStream.setupDefaultSecurity(stream);
        stream.allowTypes(new Class[]{clazz});
        stream.processAnnotations(new Class[]{clazz});
        stream.setMode(XStream.NO_REFERENCES);
        stream.alias("xml", clazz);
        return (T) stream.fromXML(resultXml);
    }

    /**
     * 创建信息模板
     * @param toUserName
     * @param fromUserName
     * @return
     */
    public static String buildMessage(String toUserName, String fromUserName, String content) {
        TextMessage build = TextMessage.builder()
                .toUserName(fromUserName)
                .fromUserName(toUserName)
                .createTime(System.currentTimeMillis() / 1000)
                .msgType("text")
                .content(content)
                .build();
        return beanToXML(build);
    }
}
