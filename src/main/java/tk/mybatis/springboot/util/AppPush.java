package tk.mybatis.springboot.util;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import tk.mybatis.springboot.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yakami
 * on 2016/8/3.
 */
public class AppPush {

    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private static String appId = "r4VubK1WoT9CaRwoa1rXh3";
    private static String appKey = "ftvAtjyspO6XpNhSp6qUe6";
    private static String masterSecret = "6tppzu4Itn6FPS4BGNxb26";
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void pushTransmission(String str) {
        pushTransmission(str, null);
    }

    public static void pushTransmission(String str, List<String> tag) {
        IGtPush push = new IGtPush(url, appKey, masterSecret);

        // 定义"点击链接打开通知模板"，并设置标题、内容、链接
        TransmissionTemplate template = transmissionTemplate(str);

        List<String> appIds = new ArrayList<String>();
        appIds.add(appId);

        // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
        AppMessage message = new AppMessage();
        message.setData(template);
        message.setAppIdList(appIds);
        message.setOffline(true);
        message.setOfflineExpireTime(1000 * 600);

        if (tag != null && tag.size() != 0)
            message.setTagList(tag);

        IPushResult ret = push.pushMessageToApp(message);
        Application.logger.info(ret.getResponse().toString());
    }

    private static TransmissionTemplate transmissionTemplate(String str) {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(str);
        // 设置定时展示时间
        // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
        return template;
    }

}
