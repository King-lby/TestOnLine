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

    //��ֹsqlע��,���û��Ƿ�����ĵ�������\��б������ת�壬�Ӷ��ﵽ�˷�ֹsqlע���Ŀ��
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
        //�������ݿ�
        Connection conn = null;
        PreparedStatement pre = null;
        ResultSet rs = null;
        try {
            //conn=DriverManager.getConnection(url);

            conn = getConnection();
            //3.��ȡstatement����
            pre = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            fillstatement(pre, params);
            //4.ʹ��Statement���󷽷�executeUpdate
            pre.executeUpdate();
            //5.��ȡ����
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

            release(conn, (Statement) pre, rs);
        }
        return list;
    }

    public User state(User user, String role) {

        String username = user.getUsername();

        return findByNumber(username, role);
    }

    //���˺Ų���
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
        //��number����params[],��list��ʽ��sql��������
        Object params[] = {username};

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

            release(conn, (Statement) pre, rs);
        }

        //list��Ϊ��
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
