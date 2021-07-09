package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphaDaoHiebernateImpl")
public class AlphaDaoHiebernateImpl implements AlphaDao {

    @Override
    public String select() {
        return "Hiebernate";
    }
}
