package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AlphaServiceImpl implements AlphaService{
    @Autowired
    private AlphaDao alphaDao;

    @Override
    public String find() {
        String s = alphaDao.select();
        return s;
    }

}
