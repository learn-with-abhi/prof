package io.letstry.prof.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.letstry.prof.service.ProfApiService;

@RestController
@RequestMapping("prof-api")
public class ProfApiController {

	@Autowired
	private ProfApiService profApiService;

	@PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file,
			@RequestParam("isHeaderPresent") boolean isHeaderPresent, @RequestParam("columnHeader") int colHeader)
			throws IOException {
		if (file.isEmpty()) {
			return new ResponseEntity<>("Please upload a non-empty file!!", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(profApiService.uploadService(file, isHeaderPresent, colHeader), HttpStatus.OK);
	}

	@PostMapping("/upload/headers") // //new annotation since 4.3
	public ResponseEntity<Object> getMaskedFile(@RequestParam("file") MultipartFile file,
			@RequestParam("isHeaderPresent") boolean isHeaderPresent, @RequestParam("columnHeader") int colHeaderRow,
			@RequestParam("selectedHeaders") Map<Integer, String> columnHeaders) throws IOException {

		if (file.isEmpty()) {
			return new ResponseEntity<>("Please upload a non-empty file!!", HttpStatus.BAD_REQUEST);
		}

		ByteArrayInputStream in = profApiService.maskSelectedColumns(file, isHeaderPresent, colHeaderRow,
				columnHeaders);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment");
		headers.add("filename", file.getOriginalFilename());
		headers.add("Content-Type", "multipart/form-data");

		profApiService.clearAll();
		return new ResponseEntity<>(new InputStreamResource(in), headers, HttpStatus.OK);
	}

}
