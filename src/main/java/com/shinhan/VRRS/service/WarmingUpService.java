package com.shinhan.VRRS.service;

import com.shinhan.VRRS.entity.Product;
import com.shinhan.VRRS.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarmingUpService {
    private final ProductRepository productRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        try {
            List<Product> products = productRepository.findAll();
            log.info("Warm-up successful, retrieved {} products.", products.size());
        } catch (Exception e) {
            log.error("Warm-up request failed: {}", e.getMessage(), e);
        }
    }
}