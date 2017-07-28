package com.practice;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.practice.service.StorageService;

@SpringBootApplication
public class SpringBootUploadAssignmentApplication implements CommandLineRunner{

	@Resource
	StorageService storageService;


	public static void main(String[] args) {
		SpringApplication.run(SpringBootUploadAssignmentApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		storageService.deleteAll();
		storageService.init();
	}

}
