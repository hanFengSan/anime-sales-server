package tk.mybatis.springboot.pojo;

import tk.mybatis.springboot.bean.DiscRank;
import tk.mybatis.springboot.bean.RawTimeRankContainer;
import tk.mybatis.springboot.bean.TimeRankContainer;

import java.util.*;

/**
 * Created by Yakami on 2016/8/1, enjoying it!
 */

public class DiscRankContainer {

    private ArrayList<TimeRankContainer> mRankList = new ArrayList<>();
    private Map<Integer, DiscRank> mRankMap = new HashMap<>();

    public DiscRankContainer() {
    }

    public DiscRankContainer(List<RawTimeRankContainer> list) {
        for (RawTimeRankContainer item : list) {
            mRankList.add(new TimeRankContainer(item));
        }
        mRankMap = getAllRankMap();
    }

    public ArrayList<TimeRankContainer> getRankList() {
        return mRankList;
    }

    public List<DiscRank> getAllRankList() {
        ArrayList<DiscRank> list = new ArrayList<>();
        for (TimeRankContainer container : mRankList) {
            if (container.getsName().equals("top_100"))
                continue;
            list.addAll(container.getDiscs());
        }
        //因为top100里会存在和其他季番列表里一样的重复项，需要特殊 处理
        List<DiscRank> cloneList = (ArrayList<DiscRank>) list.clone();
        for (TimeRankContainer container : mRankList) {
            if (container.getsName().equals("top_100")) {
                List<DiscRank> topList = container.getDiscs();
                for (DiscRank item : topList) {
                    boolean isExisted = false;
                    for (Iterator it = cloneList.iterator(); it.hasNext(); ) {
                        DiscRank tmp = (DiscRank) it.next();
                        if (tmp.getId() == item.getId()) {
                            it.remove();
                            isExisted = true;
                            break;
                        }
                    }
                    if (!isExisted)
                        list.add(item);
                }
            }
        }
        return list;
    }

    public Map<Integer, DiscRank> getRankMap() {
        return mRankMap;
    }

    private Map<Integer, DiscRank> getAllRankMap() {
        Map<Integer, DiscRank> result = new HashMap<>();
        for (TimeRankContainer container : mRankList) {
            for (DiscRank item : container.getDiscs()) {
                if (!result.containsKey(item.getId())) {
                    result.put(item.getId(), item);
                }
            }
        }
        return result;
    }

    public List<DiscRank> updateList(DiscRankContainer container) {
        List<DiscRank> result = new ArrayList<>();

        for (Map.Entry<Integer, DiscRank> entry : container.getRankMap().entrySet()) {
            DiscRank newItem = entry.getValue();
            if (mRankMap.containsKey(entry.getKey())) {
                DiscRank oldItem = this.getRankMap().get(entry.getKey());
                if (newItem.getUpdateDate().compareTo(oldItem.getUpdateDate()) == 1) {
                    result.add(newItem);
                    mRankMap.remove(oldItem.getId());
                    mRankMap.put(newItem.getId(), newItem);
                }
            } else {
                result.add(newItem);
                mRankMap.put(newItem.getId(), newItem);
            }
        }
        return result;
    }

    public List<DiscRank> getUpdatedListAndUpdate(DiscRankContainer container) {
        List<DiscRank> result = new ArrayList<>();
        List<DiscRank> originalList = getAllRankList();
        List<DiscRank> newList = (List<DiscRank>) ((ArrayList) container.getAllRankList()).clone();

        for (DiscRank newItem : newList) {
            boolean isExisted = false;
            for (DiscRank oldItem : originalList) {
                if (newItem.getId() == oldItem.getId()) {
                    if (newItem.getUpdateDate().compareTo(oldItem.getUpdateDate()) == 1) {
                        result.add(newItem);
                        oldItem.update(newItem);
                    }
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                result.add(newItem);

                originalList.add(newItem);
            }
        }
        return result;
    }

    public void updateList(List<DiscRank> oldList, List<DiscRank> updateList) {
        for (DiscRank updateItem : updateList) {
            for (DiscRank item : oldList) {
                if (item.getId() == updateItem.getId()) {
                    item.update(updateItem);
                }
            }
        }
    }


    public void setRankList(ArrayList<TimeRankContainer> rankList) {
        mRankList = rankList;
    }

}
