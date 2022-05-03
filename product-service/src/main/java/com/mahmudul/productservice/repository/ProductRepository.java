package com.mahmudul.productservice.repository;

import com.mahmudul.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository  extends JpaRepository<Product, String> {
}
