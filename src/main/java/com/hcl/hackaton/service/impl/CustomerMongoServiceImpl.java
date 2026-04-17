package com.hcl.hackaton.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hcl.hackaton.entity.CustomerDocument;
import com.hcl.hackaton.mongo.repository.CustomerMongoRepository;
import com.hcl.hackaton.service.CustomerMongoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerMongoServiceImpl implements CustomerMongoService {

	private final CustomerMongoRepository repository;

	@Override
	public CustomerDocument save(CustomerDocument document) {
		return repository.save(document);
	}

	@Override
	public List<CustomerDocument> getAll() {
		return repository.findAll();
	}

	@Override
	public CustomerDocument getById(String id) {
		return repository.findById(id).orElseThrow(() -> new RuntimeException("Document not found"));
	}
}