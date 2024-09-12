package com.shinhan.VRRS.service.productService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product newProduct(String jsonData) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonData, Product.class);
    }

    public Boolean checkName(String name) {
        return productRepository.existsByName(name);
    }

    public void insertProduct(Product product, String imagePath) {
        product.setImgPath(imagePath); // 이미지 경로 설정
        productRepository.save(product);
    }

    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProducts(String name) {
        return productRepository.findByNameContaining(name);
    }

    // isRec: rec 업데이트(true), notRec 업데이트(false)
    // isIncrement: 증가(true), 감소(flase)
    public Product updateProductReview(Long id, boolean isRec, boolean isIncrement) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (isIncrement) {
            if (isRec) product.setRec(product.getRec() + 1);
            else product.setNotRec(product.getNotRec() + 1);
        }
        else { // 값이 0 미만으로 내려가지 않도록 설정
            if (isRec) product.setRec(Math.max(product.getRec() - 1, 0));
            else product.setNotRec(Math.max(product.getNotRec() - 1, 0));
        }

        return productRepository.save(product);
    }
}
