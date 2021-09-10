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

import com.lby.domain.User;
import com.lby.utils.JDBCUtils;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;

public class UserDao extends JDBCUtils{

    //防止sql注入,把用户非法输入的单引号用\反斜杠做了转义，从而达到了防止sql注入的目的
    private void fillstatement(PreparedStatement pre, Object[] params) throws SQLException {

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pre.setObject(i + 1, params[i]);

            }
        }
    }

    private void release(Connection conn, Statement stmt, ResultSet rs) {

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

    private List<Map<String, Object>> RsTolist(ResultSet rs) throws SQLException {

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

    public void insertUser(User auser, String role) {

        String sql = "";
        if (role.equals("student")) {
            sql = "insert into student(username,name,password,age,depart) values( ?,?,?,?,? )";
        } else if (role.equals("teacher")) {
            sql = "insert into teacher(username,name,password,age,depart) values( ?,?,?,?,? )";
        } else if (role.equals("admin")) {
            sql = "insert into admin(username,name,password,age,depart) values( ?,?,?,?,? )";
        }
        Object params[] = {
                auser.getUsername(), auser.getName(), auser.getPassword(), auser.getAge(), auser.getDepart()
        };
        //连接数据库
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        try {
            //conn=DriverManager.getConnection(url);

            conn = getConnection();
            //3.获取statement连接
            pre = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            fillstatement(pre, params);
            //4.使用Statement对象方法executeUpdate
            pre.executeUpdate();
            //5.获取主键
            rs = pre.getGeneratedKeys();
            Object key = null;
            if (rs.next()) {
                key = rs.getObject(1);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        } finally {
            release(conn, (Statement) pre, rs);
        }
    }

    public List<Map<String, Object>> login(User user, String role) {

        String sql = "";
        if (role.equals("student")) {
            sql = "select * from student where username=? and password=? ";
        } else if (role.equals("teacher")) {
            sql = "select * from teacher where username=? and password=? ";
        } else if (role.equals("admin")) {
            sql = "select * from admin where username=? and password=? ";
        }
        Object params[] = {user.getUsername(), user.getPassword()};

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

            release(conn, (Statement) pre, rs);
        }
        return list;
    }

    public User state(User user, String role) {

        String username = user.getUsername();

        return findByNumber(username, role);
    }

    //按账号查找
    public User findByNumber(String username, String role) {
        User user = null;
        String sql = "";
        if (role.equals("student")) {
            sql = "select * from student where username=?";
        } else if (role.equals("teacher")) {
            sql = "select * from teacher where username=?";
        } else if (role.equals("admin")) {
            sql = "select * from admin where username=?";
        }
        //将number存入params[],以list形式与sql连结起来
        Object params[] = {username};

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

            release(conn, (Statement) pre, rs);
        }

        //list不为空
        if (!list.isEmpty()) {
            user = new User();
            user.setId((Integer) list.get(0).get("id"));
            user.setUsername((String) list.get(0).get("username"));
            user.setPassword((String) list.get(0).get("password"));
            user.setState((Integer) list.get(0).get("state"));
            user.setName((String) list.get(0).get("name"));
            user.setDepart((String) list.get(0).get("depart"));
            user.setAge((Integer) list.get(0).get("age"));
        }
        System.out.println("list" + list);
        System.out.println("user" + user);
        return user;

    }


}
