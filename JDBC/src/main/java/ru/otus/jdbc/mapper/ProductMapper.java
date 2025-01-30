package ru.otus.jdbc.mapper;

import org.mapstruct.Mapper;
import ru.otus.jdbc.dto.ProductDTO;
import ru.otus.jdbc.entity.Product;

import java.util.List;

@Mapper(imports = {Product.class, ProductDTO.class}, componentModel = "spring")
public interface ProductMapper {

    Product DTOtoEntity(ProductDTO productDTO);

    ProductDTO entityToDTO(Product product);

    List<ProductDTO> entityToDTO(List<Product> products);

}