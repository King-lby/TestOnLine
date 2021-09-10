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

        //将ResultSet对象转换为list
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //获取表结构
        ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
        //通过rs逐条读取记录
        while (rs.next()) {
            //每条记录放入1个Map中
            Map<String, Object> map = new HashMap<String, Object>();
            //rsmd.getColumCount(),字段的列数
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                //逐个字段读取出来放入map中,以键值形式存入，字段名：rsmd.getColumnName(i)，对应值 rs.getObject(i)
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
//连接数据库
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
//3.获取statement连接
            pre = conn.prepareStatement(sql);

            fillstatement(pre, params);
//4.使用Statement对象方法executeQuery
            rs = pre.executeQuery();
            //通过rs逐条读取记录
            while (rs.next()) {
                //每条记录放入1个Map中
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
            //关闭连接
            release(conn, (Statement) pre, rs);
        }

        return list1;
    }

    public String findclass(String username) {
        String sql = "";


        sql = "select class from student WHERE username=? ";


        Object params[] = {username};

        //连接数据库
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        Object result = null;
        //2.获取数据库链接
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
            //3.获取statement连接
            pre = conn.prepareStatement(sql);
            fillstatement(pre, params);
            //4.使用Statement对象方法executeQuery
            rs = pre.executeQuery();

            if (rs.next()) {
                result = rs.getObject(1);
            }


        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            //关闭连接
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
        //连接数据库
        ResultSet rs = null;
        Connection conn = null;
        PreparedStatement pre = null;
        List<Map<String, Object>> list = null;
        //2.获取数据库链接
        try {
            //conn=DriverManager.getConnection(url);
            conn = getConnection();
            //3.获取statement连接
            pre = conn.prepareStatement(sql);
            fillstatement(pre, params);
            //4.使用Statement对象方法executeQuery
            rs = pre.executeQuery();
            list = RsTolist(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //关闭连接
            release(conn, (Statement) pre, rs);
        }
        return list;
    }


}
