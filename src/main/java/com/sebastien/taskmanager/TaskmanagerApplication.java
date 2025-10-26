package com.sebastien.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = {"com.sebastien.taskmanager.config",
		"com.sebastien.taskmanager.controller",
		"com.sebastien.taskmanager.converter",
		"com.sebastien.taskmanager.entity",
		"com.sebastien.taskmanager.enums",
		"com.sebastien.taskmanager.exceptions",
		"com.sebastien.taskmanager.repository",
		"com.sebastien.taskmanager.service"})
@EnableWebMvc
public class TaskmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}

}
