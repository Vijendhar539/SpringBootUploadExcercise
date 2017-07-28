package com.practice.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.practice.service.StorageService;


@Controller
public class UploadController {

	@Autowired
	StorageService storageService;

	List<String> files = new ArrayList<String>();

	@RequestMapping("/")
	public String listUploadedFiles(Model model) {
		return "uploadForm";
	}

	@PostMapping("/api/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile inputFile, Model model) {
		try {
			storageService.store(inputFile);
			model.addAttribute("message", "You have successfully uploaded " + inputFile.getOriginalFilename());
			files.add(inputFile.getOriginalFilename());
		} catch (Exception e) {
			model.addAttribute("message", "FAIL to upload " + inputFile.getOriginalFilename());
		}
		return "uploadForm";
	}

	@PostMapping("/api/multiupload")
	public String handleFileMultiUploads(@RequestParam("files") MultipartFile[] inputFiles, Model model) {
		 String uploadedFileNames = Arrays.stream(inputFiles).map(x -> x.getOriginalFilename())
	                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(","));
		
		try {
			storageService.storeFiles(Arrays.asList(inputFiles));
			 model.addAttribute("message", "You have successfully uploaded " + uploadedFileNames + "!");
			files.addAll(Arrays.asList(uploadedFileNames.split(",")));
		} catch (Exception e) {
			model.addAttribute("message", "FAIL to upload " + uploadedFileNames);  
		}
		return "uploadForm";
	}

	@GetMapping("/api/listFiles")
	public String getListFiles(Model model) {
		model.addAttribute("files",
				files.stream()
					.map(fileName -> MvcUriComponentsBuilder
					.fromMethodName(UploadController.class, "getFile", fileName).build().toString())
					.collect(Collectors.toList()));
		model.addAttribute("totalFiles", "TotalFiles: " + files.size());
		return "listFiles";
	}

	@GetMapping("/api/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		Resource file = storageService.loadFile(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}