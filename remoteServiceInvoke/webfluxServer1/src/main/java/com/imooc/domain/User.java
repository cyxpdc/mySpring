package com.imooc.domain;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "user") //表名
@Data
public class User {

	@Id
	private String id; //mongodb的id一般为String

	@NotBlank
	private String name;

	@Range(min=10, max=100)
	private int age;

}
