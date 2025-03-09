package com.javalord.MyPhotoAppApiUsers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MyPhotoAppApiUsersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyPhotoAppApiUsersApplication.class, args);
	}

}
