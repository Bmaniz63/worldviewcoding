package com.balansys.springbt_krb5_databases_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringbtKrb5DatabasesDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbtKrb5DatabasesDemoApplication.class, args);

		//System.out.println(sayHi());
		System.out.println(doSomething());
	}

	public static String sayHi(){
		return  "Hello There";
	}

	public static int doSomething() {

		return 1;
	}

}