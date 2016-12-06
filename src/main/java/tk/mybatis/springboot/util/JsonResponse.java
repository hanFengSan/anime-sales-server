package tk.mybatis.springboot.util;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Yakami
 * on 2016/6/16.
 */
public class JsonResponse {

    public static String getJson(String key, Object value) {
        JSONObject tmp = new JSONObject();
        tmp.put(key, value);
        return tmp.toJSONString();
    }
}
