package com.estafet.boostcd.feature.api.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.estafet.boostcd.feature.api.model.Product;

@Repository
public class ProductDAO {

	@PersistenceContext
	private EntityManager entityManager;
		
	public Product getProduct(String productId) {
		return entityManager.find(Product.class, productId);
	}

	public Product create(Product product) {
		entityManager.persist(product);
		return product;
	}
	
	public void deleteProduct(String productId) {
		Product product = getProduct(productId);
		if (product != null) {
			entityManager.remove(product);	
		}
	}
	
}
