package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.ProductsDto;
import com.codewithmosh.store.entities.Product;
import com.codewithmosh.store.mappers.ProductMapper;
import com.codewithmosh.store.repositories.CategoryRepository;
import com.codewithmosh.store.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductMapper productMapper;

  @GetMapping
  public List<ProductsDto> findAll(
      @RequestParam(required = false, defaultValue = "", name = "categoryId") String categoryId) {
    List<Product> products;
    if (categoryId.isEmpty()) {
      products = productRepository.findAll();
    } else {
      products = productRepository.findAllByCategoryId(Long.valueOf(categoryId));
    }
    return products.stream().map(productMapper::productToDto).toList();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductsDto> findById(@PathVariable Long id) {
    var product = productRepository.findById(id).orElse(null);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }

    val productDto = productMapper.productToDto(product);
    return ResponseEntity.ok(productDto);
  }

  @PostMapping()
  public ResponseEntity<ProductsDto> createProduct(
      @RequestBody ProductsDto productsDto, UriComponentsBuilder uriBuilder) {
    var category = categoryRepository.findById(productsDto.getCategoryId()).orElse(null);
    if (category == null) {
      return ResponseEntity.badRequest().build();
    }

    var product = productMapper.productDtoToEntity(productsDto);
    product.setCategory(category);
    productRepository.save(product);
    productsDto.setId(product.getId());

    var uri = uriBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri();

    return ResponseEntity.created(uri).body(productsDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductsDto> updateProduct(
      @PathVariable(name = "id") Long id, @RequestBody ProductsDto productsDto) {
    var product = productRepository.findById(id).orElse(null);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }

    productMapper.updateProduct(productsDto, product);
    productRepository.save(product);

    return ResponseEntity.ok(productMapper.productToDto(product));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ProductsDto> deleteProduct(@PathVariable(name = "id") Long id) {
    var product = productRepository.findById(id).orElse(null);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }

    productRepository.delete(product);
    return ResponseEntity.noContent().build();
  }
}
