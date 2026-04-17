package com.hcl.hackaton.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "customers")
@Data
public class CustomerDocument {

	@Id
	private String id;
	private String name;
}
