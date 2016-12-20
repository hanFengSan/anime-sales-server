package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

import java.util.List;

/**
 * Created by Yakami on 16/12/17.
 * Emm...
 */
public class SalesContainer<T extends Entity> {
    private List<T> list;
    private long updateTime;
    private long nextUpdateTime;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getNextUpdateTime() {
        return nextUpdateTime;
    }

    public void setNextUpdateTime(long nextUpdateTime) {
        this.nextUpdateTime = nextUpdateTime;
    }
}
