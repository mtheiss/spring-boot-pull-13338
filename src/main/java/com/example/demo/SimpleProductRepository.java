package com.example.demo;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SimpleProductRepository {
	@Cacheable("product")
	public Product getProductById(Long id) {
		Product p = new Product(id, "product-" + id);
		// artificial delay
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return p;
	}
}
