package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.ProductsDto;
import com.codewithmosh.store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
  @Mapping(source = "category.id", target = "categoryId")
  ProductsDto productToDto(Product product);

  Product productDtoToEntity(ProductsDto productsDto);

  @Mapping(target = "id", ignore = true)
  void updateProduct(ProductsDto productsDto, @MappingTarget Product product);
}
