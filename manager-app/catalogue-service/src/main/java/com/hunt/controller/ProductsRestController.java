package com.hunt.controller;

import com.hunt.controller.payload.NewProductPayload;
import com.hunt.entity.Product;
import com.hunt.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("catalogue-api/products")
@RequiredArgsConstructor
public class ProductsRestController {

    private final ProductService productService;

    @GetMapping
    public List<Product> findProducts() {
        return this.productService.findAllProducts();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProduct(@Valid @RequestBody NewProductPayload payload, BindingResult bindingResult, UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException exception){
                throw exception;
            }
            else {
                throw new BindException(bindingResult);
            }
        }
        else {
           Product product = this.productService.createProduct(payload.title(), payload.details());
           return ResponseEntity
                   .created(uriComponentsBuilder
                           .replacePath("/catalogue-api/products/{productId}")
                           .build(Map.of("productId", product.getId())))
                   .body(product);
        }
    }

}
