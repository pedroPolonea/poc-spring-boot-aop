package com.sb.ap.resource;

import com.sb.ap.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping(value = "/product")
public class ProductResource {

    private ProductService productService;

    @Autowired
    public ProductResource(final ProductService productService){
        this.productService = productService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable(value = "id") final Long id){
        AtomicReference<ResponseEntity> responseEntity = new AtomicReference<ResponseEntity>();

        productService.getById(id).ifPresentOrElse(product -> {
                    log.info("M=findById, I=Sucesso, product={}", product);
                    responseEntity.set(ResponseEntity.ok(product));
                },
                () -> {
                    log.error("M=findById, E=Erro, id={}", id);
                    responseEntity.set(ResponseEntity.notFound().build());
        });

        return responseEntity.get();
    }
}
