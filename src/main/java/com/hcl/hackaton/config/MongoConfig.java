package com.hcl.hackaton.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.hcl.hackaton.mongo.repository")
public class MongoConfig {
}
