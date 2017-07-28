package com.practice;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.practice.service.StorageFileNotFoundException;
import com.practice.service.StorageService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootUploadAssignmentApplicationTests {

	   @Autowired
	    private MockMvc mvc;

	    @MockBean
	    private StorageService storageService;


	    @Test
	    public void shouldSaveUploadedFile() throws Exception {
	        MockMultipartFile multipartFile =
	                new MockMultipartFile("file", "uploadFile.txt", "text/plain", "Spring Boot Upload".getBytes());
	        this.mvc.perform(fileUpload("/api/upload")
	        		.file(multipartFile))
	                .andExpect(status().isOk());

	        then(this.storageService).should().store(multipartFile);
	    }

	    @Test
	    public void should404WhenFileisNotFound() throws Exception {
	        given(this.storageService.loadFile("uploadFile.txt"))
	                .willThrow(StorageFileNotFoundException.class);

	        this.mvc.perform(get("/files/uploadFile.txt"))
	                .andExpect(status().isNotFound());
	    }

}
