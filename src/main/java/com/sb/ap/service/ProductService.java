package com.sb.ap.service;

import com.sb.ap.model.Product;

import java.util.Optional;

public interface ProductService {

    Optional<Product> getById(Long id);
}
