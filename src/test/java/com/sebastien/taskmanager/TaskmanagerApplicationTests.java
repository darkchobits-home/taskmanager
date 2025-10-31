package com.sebastien.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TaskmanagerApplication.class)
@TestPropertySource(locations = "classpath:application-integrationtest.yml")
class TaskmanagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
