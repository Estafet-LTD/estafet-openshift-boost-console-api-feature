package com.estafet.boostcd.feature.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Product")
public class Product {
    
    @Id
	@Column(name = "PRODUCT_ID", nullable = false)
    private String productId;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Env> envs = new ArrayList<Env>();

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

	public List<Env> getEnvs() {
        return envs;
    }

	public Product addEnv(Env env) {
		env.setProduct(this);
		envs.add(env);
		return this;
    }

    public static ProductBuilder builder() {
        return new ProductBuilder();
    }
    
    public static class ProductBuilder {

        private String productId;

		public ProductBuilder setProductId(String productId) {
            this.productId = productId;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setProductId(productId);
            return product;
        }
        
    }

}