package com.hcl.hackaton.service;



import java.util.List;

import com.hcl.hackaton.entity.CustomerDocument;

public interface CustomerMongoService {

	CustomerDocument save(CustomerDocument document);

	List<CustomerDocument> getAll();


	CustomerDocument getById(String id);
}