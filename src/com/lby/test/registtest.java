package com.lby.test;

import com.lby.domain.User;
import com.lby.service.UserService;

public class registtest {
    public static void main(String[] args) {
    	String role="student";
    	String name="hgh";
    	String password="123";
    	String username="123";
    	String depart="计算机科学与技术";
    	int age=21;
    	UserService userService=new UserService();
    	User user=new User();
    	user.setAge(age);
    	user.setDepart(depart);
    	user.setName(name);
    	user.setPassword(password);
    	user.setUsername(username);
    	userService.regist(user, role);
	}
	
	
}
