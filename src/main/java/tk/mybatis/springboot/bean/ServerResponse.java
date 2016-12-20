package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.Application;
import tk.mybatis.springboot.bean.base.Entity;

/**
 * Created by Yakami on 2016/8/3, enjoying it!
 */

public class ServerResponse extends Entity {

    private int code;
    private String message;
    private String data;

    public ServerResponse(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
        Application.logger.info("Data: " + this.data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}