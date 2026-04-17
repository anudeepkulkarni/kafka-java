package com.hcl.hackaton.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hcl.hackaton.entity.CustomerDocument;

public interface CustomerMongoRepository extends MongoRepository<CustomerDocument, String> {
}