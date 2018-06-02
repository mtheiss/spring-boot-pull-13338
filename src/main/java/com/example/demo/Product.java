package com.example.demo;

import java.io.Serializable;

/**
 * Sample POJO
 */
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Long id;
	private final String name;
	
	public Product(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
