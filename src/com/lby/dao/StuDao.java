package com.lby.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.lby.domain.Question;
import com.lby.utils.JDBCUtils;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;

public class StuDao extends JDBCUtils {

    private static void fillstatement(PreparedStatement pre, Object[] params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pre.setObject(i + 1, params[i]);
            }
        }
    }

    private static void release(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        Connection tconn = tl.get();
                        if (tconn == conn) {
                            return;
                        }
                        if (tconn != null) {
                            try {
                                conn.close();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                }
            }
        }
    }

    private static List<Map<String, Object>> RsTolist(ResultSet rs) throws SQLException {

        //��ResultSet����ת��Ϊlist
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //��ȡ��ṹ
        ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
        //ͨ��rs������ȡ��¼
        while (rs.next()) {
            //ÿ����¼����1��Map��
            Map<String, Object> map = new HashMap<String, Object>();
            //rsmd.getColumCount(),�ֶε�����
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                //����ֶζ�ȡ��������map��,�Լ�ֵ��ʽ���룬�ֶ�����rsmd.getColumnName(i)����Ӧֵ rs.getObject(i)
                map.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            list.add(map);
        }
        return list;
    }

    public List<Question> findAllCourse(String coursename) {
        List<Question> list1 = new ArrayList<Question>();

        String sql = "";

        sql = "select * from question  where  coursename = ? ";


        Object params[] = {
                coursename
        };
//�������ݿ�
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
//3.��ȡstatement����
            pre = conn.prepareStatement(sql);

            fillstatement(pre, params);
//4.ʹ��Statement���󷽷�executeQuery
            rs = pre.executeQuery();
            //ͨ��rs������ȡ��¼
            while (rs.next()) {
                //ÿ����¼����1��Map��
                Question tea = new Question();
                tea.setId(rs.getInt("id"));

                tea.setCoursename(rs.getString("coursename"));
                tea.setQuestionmatter(rs.getString("questionmatter"));
                tea.setQuestionname(rs.getString("questionname"));
                tea.setAnswer(rs.getString("answer"));
                tea.setLevel(rs.findColumn("level"));
                list1.add(tea);
            }
            for (Question teacherBean : list1) {

                System.out.println(teacherBean);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            //�ر�����
            release(conn, (Statement) pre, rs);
        }

        return list1;
    }

    public String findclass(String username) {
        String sql = "";


        sql = "select class from student WHERE username=? ";


        Object params[] = {username};

        //�������ݿ�
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        Object result = null;
        //2.��ȡ���ݿ�����
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
            //3.��ȡstatement����
            pre = conn.prepareStatement(sql);
            fillstatement(pre, params);
            //4.ʹ��Statement���󷽷�executeQuery
            rs = pre.executeQuery();

            if (rs.next()) {
                result = rs.getObject(1);
            }


        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            //�ر�����
            release(conn, (Statement) pre, rs);
        }
        System.out.println(result);

        return (String) result;
    }

    public List<Map<String, Object>> findtest(String coursename, String type, String action) {
        String sql = "";
        if ("testbyhand".equals(action)) {
            sql = "select * from maketestbyhand  where coursename =? and type='" + type + "'";
        }
        if ("testbyrandom".equals(action)) {
            sql = "select * from maketestrandom  where coursename =? and type='" + type + "'";
        }

        Object params[] = {
                coursename
        };
        //�������ݿ�
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        List<Map<String, Object>> list = null;
        //2.��ȡ���ݿ�����
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
            //3.��ȡstatement����
            pre = conn.prepareStatement(sql);
            fillstatement(pre, params);
            //4.ʹ��Statement���󷽷�executeQuery
            rs = pre.executeQuery();
            list = RsTolist(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //�ر�����
            release(conn, (Statement) pre, rs);
        }
        return list;
    }


}
