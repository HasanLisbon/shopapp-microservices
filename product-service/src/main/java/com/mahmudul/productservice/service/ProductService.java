package com.mahmudul.productservice.service;

import com.mahmudul.productservice.dto.ProductRequest;
import com.mahmudul.productservice.dto.ProductResponse;
import com.mahmudul.productservice.model.Product;
import com.mahmudul.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest){
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product {} is svaed", product.getId());
    }

    public List<ProductResponse> getProducts(){
        List<Product> products = productRepository.findAll();

        return products.stream().map(this:: mapToProductRespose).collect(Collectors.toList());
    }

    private ProductResponse mapToProductRespose(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
