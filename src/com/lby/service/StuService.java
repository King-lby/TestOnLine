package com.lby.service;

import java.util.List;
import java.util.Map;

import com.lby.dao.StuDao;
import com.lby.domain.Page;
import com.lby.domain.Question;

public class StuService {

    StuDao stuDao = new StuDao();

    public List<Question> findAllQuestion(String coursename) {
        return stuDao.findAllCourse(coursename);
    }

    public String findclass(String username) {

        return stuDao.findclass(username);
    }

    public Page findtest(String coursename, String type, String action) {
        Page page = new Page(0, 0);

        //����Dao��ȡ�б�
        List<Map<String, Object>> list = stuDao.findtest(coursename, type, action);

        page.setList(list);
        //����ȡ���б����ø�Page��list����
        System.out.println(page);


        return page;
    }
}
