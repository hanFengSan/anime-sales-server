package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

/**
 * Created by Yakami on 2016/8/2, enjoying it!
 */

public class SettingProfile extends Entity {

    //notification 配置
    private boolean hasVibrate = true;
    private boolean hasSound = true;
    private boolean hasLight = true;

    private boolean isBlocking = false; //屏蔽推送
    private boolean isNightMode = false;

    public static final String CACHE_NAME = "SETTING";

    public boolean isHasVibrate() {
        return hasVibrate;
    }

    public void setHasVibrate(boolean hasVibrate) {
        this.hasVibrate = hasVibrate;
    }

    public boolean isHasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }

    public boolean isHasLight() {
        return hasLight;
    }

    public void setHasLight(boolean hasLight) {
        this.hasLight = hasLight;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public void setBlocking(boolean blocking) {
        isBlocking = blocking;
    }

    public boolean isNightMode() {
        return isNightMode;
    }

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }
}
