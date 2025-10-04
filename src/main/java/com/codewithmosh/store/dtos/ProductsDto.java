package com.codewithmosh.store.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductsDto {
  private Long id;
  private String name;
  private String description;
  private String price;
  private Byte categoryId;
}
