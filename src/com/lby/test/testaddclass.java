package com.lby.test;

import com.lby.service.SecService;

public class testaddclass {
	 public static void main(String[] args) {
	    	
	    SecService secService=new SecService();
	  /*  Class1 class1=new Class1();
	    class1.setClassname("计科172班");
	    class1.setDepart("计算机科学与技术");
	    class1.setManager("");
	    class1.setPeople(41);
	    	secService.addclass(class1);
		}*/
	    secService.find("计算机科学与技术", "", 1);
	   
}
}
