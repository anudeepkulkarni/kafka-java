package com.hcl.hackaton.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.hackaton.entity.CustomerDocument;
import com.hcl.hackaton.service.CustomerMongoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mongo")
@RequiredArgsConstructor
public class CustomerMongoController {

	private final CustomerMongoService service;

	// ✅ Save document
	@PostMapping("/save")
	public CustomerDocument save(@RequestBody CustomerDocument document) {
		return service.save(document);
	}

	// ✅ Get all documents
	@GetMapping("/all")
	public List<CustomerDocument> getAll() {
		return service.getAll();
	}

	// ✅ Get by ID
	@GetMapping("/{id}")
	public CustomerDocument getById(@PathVariable String id) {
		return service.getById(id);
	}
}