package tk.mybatis.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.springboot.mapper.MatchesMapper;
import tk.mybatis.springboot.model.Matches;

import java.util.List;

/**
 * Created by Yakami
 * on 2016/6/16.
 */
@Service
public class MatchesService {

    @Autowired
    private MatchesMapper matchMapper;

    public List<Matches> getAll() {
        return matchMapper.selectAll();
    }

    public Matches getById(Integer id) {
        return matchMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        matchMapper.deleteByPrimaryKey(id);
    }

    public void save(Matches match) {
        if (match.getId() != null) {
            matchMapper.updateByPrimaryKey(match);
        } else {
            matchMapper.insert(match);
        }
    }
}
