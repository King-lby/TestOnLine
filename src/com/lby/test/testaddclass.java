package com.lby.test;

import com.lby.service.SecService;

public class testaddclass {
	 public static void main(String[] args) {
	    	
	    SecService secService=new SecService();
	  /*  Class1 class1=new Class1();
	    class1.setClassname("�ƿ�172��");
	    class1.setDepart("�������ѧ�뼼��");
	    class1.setManager("");
	    class1.setPeople(41);
	    	secService.addclass(class1);
		}*/
	    secService.find("�������ѧ�뼼��", "", 1);
	   
}
}
