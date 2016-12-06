package tk.mybatis.springboot.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.validation.FieldError;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yakami
 * on 2016/6/16.
 */
public class ResponseUtils {

    private static final Logger logger = Logger.getLogger(ResponseUtils.class);

    /**
     *
     * @return
     */
    public static JSONObject getJsonObject(){
        return new JSONObject();
    }

    /**
     * 发送文本。使用UTF-8编码。
     *
     * @param response
     *            HttpServletResponse
     * @param text
     *            发送的字符串
     */
    public static void renderText(HttpServletResponse response, String text) {
        render(response, "text/plain;charset=UTF-8", text);
    }

    /**
     * 发送json。使用UTF-8编码。
     *
     * @param response
     *            HttpServletResponse
     * @param text
     *            发送的字符串
     */
    public static void renderJson(HttpServletResponse response, String text) {
        render(response, "application/json;charset=UTF-8", text);
    }

    /**
     * 发送xml。使用UTF-8编码。
     *
     * @param response
     *            HttpServletResponse
     * @param text
     *            发送的字符串
     */
    public static void renderXml(HttpServletResponse response, String text) {
        render(response, "text/xml;charset=UTF-8", text);
    }

    /**
     * 发送内容。使用UTF-8编码。
     *
     * @param response
     * @param contentType
     * @param text
     */
    public static void render(HttpServletResponse response, String contentType,
                              String text) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     * @param fieldErrors
     * @return
     */
    public static Map<String, String> copyErrors(List<FieldError> fieldErrors) {
        Map<String, String> maps = new HashMap<String, String>();
        for (FieldError error : fieldErrors){
            maps.put(error.getField(), error.getDefaultMessage());
        }
        return maps;
    }
}
