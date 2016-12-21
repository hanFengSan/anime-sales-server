#anime-sales-server
这个是[anime-sales](http://anime-sales.com)网站的后台project, 主要用于爬取oricon各种销量数据.

## 使用
在`ServerAPI.java`中补充`oricon`会员的ID和密码.

## 项目简介
project是用spring+springMVC+mybatis写的, spring boot, 注释应该还算完善, 结构如下:

**项目结构:**
```
-----project
     |-bean // 各种java bean, 如日榜项/周榜项等的bean
     |-business // 事务层
     | |-OtherDVDTableService // oricon外网的网页信息抽取服务,主要获取电影DVD&动画DVD日榜
       |-SalesService // 销量服务
       |-SalesTableService // oricon内网信息抽取服务, 主要获取全部周榜&大部分日榜
     |
     |-conf
     |-controller // Controller层
       |-SalesConroller // 销量信息获取controller
     |-model
     |-util
     |-ServerAPI // 提供retrofit封装
     |-Application
```

