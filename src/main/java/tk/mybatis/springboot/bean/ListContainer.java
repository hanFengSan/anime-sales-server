package tk.mybatis.springboot.bean;

import tk.mybatis.springboot.bean.base.Entity;

import java.util.List;

/**
 * @author Yakami, Created on 2016/4/15
 */
public class ListContainer<T extends Entity> extends Entity {
    public List<T> list;
}
