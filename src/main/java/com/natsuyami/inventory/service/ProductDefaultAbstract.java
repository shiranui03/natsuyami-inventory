package com.natsuyami.inventory.service;

import com.natsuyami.inventory.dto.ProductDetailsDto;
import com.natsuyami.inventory.dto.ProductDto;
import com.natsuyami.inventory.dto.builder.ProductDtoBuilder;
import com.natsuyami.inventory.model.Product;
import com.natsuyami.inventory.repository.ProductRepository;
import com.natsuyami.inventory.repository.ShopProductRepository;
import com.natsuyami.inventory.repository.specification.ProductSpecificationBuilder;
import com.natsuyami.inventory.service.impl.DefaultImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ProductDefaultAbstract implements DefaultImpl<ProductDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDefaultAbstract.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopProductRepository shopProductRepository;

    /**
     * Get all products
     * @return List - products
     */
    @Override
    public Page<ProductDto> getAll(Pageable pageable) {
        LOGGER.info("Initialized ProductDefaultAbstract getAll() method");

        List<ProductDto> productList = new ArrayList<>();

        Page<Product> products = productRepository.findAll(pageable);
        for (Product product : products) {
            productList.add(ProductDtoBuilder.getInstance().build(product));
        }

        Page<ProductDto> result = new PageImpl<ProductDto>(productList);
        return result;
    }

    @Override
    public ProductDetailsDto getById(long id) {
        return new ProductDetailsDto();
    }

    /**
     * search product with the given values
     * build in to the dto
     * @param search - query filter values
     * @return List - products that contains the given search filter
     */
    @Override
    public List<ProductDto> search(String search) {
        LOGGER.info("Initialized ProductDefaultAbstract search() method with search={{}}", search);

        List<Product> products = findProductsWithFilters(search);
        List<ProductDto> result = new ArrayList<>();

        LOGGER.info("Product search result size={{}}", products.size());
        for (Product product : products) {
            ProductDto productDetailsDto = ProductDtoBuilder.getInstance().build(product);
            result.add(productDetailsDto);
        }

        return result;
    }

    /**
     * find product via ID
     * @param id - product id
     * @return Product - product data containing the id
     */
    public Product findById(long id) {
        LOGGER.info("Initialized ProductDefaultAbstract findById() method with id={{}}", id);

        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()) {
            return product.get();
        }

        return null;
    }

    /**
     * search for the given values with its key and method of search
     * for example search=productName:Chip,brandName:Monde (where ':' = like)
     * @param search - contains key, operation and value to used for query
     * @return List - products that contains the given search filter
     */
    public List<Product> findProductsWithFilters(String search) {
        LOGGER.info("Initialized ProductDefaultAbstract findProductsWithKeyword() method with search={{}}", search);
        ProductSpecificationBuilder specsBuilder = new ProductSpecificationBuilder();
        Pattern pattern = Pattern.compile("(\\w+?)(=|:|<|>)(\\w+?),"); // original copy paste code (:|<|>) -> :
        Matcher matcher = pattern.matcher(search + ",");

        while (matcher.find()) {
            specsBuilder.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        Specification<Product> specsProduct = specsBuilder.build();
        List<Product> products = productRepository.findAll(specsProduct);

        return products;
    }

}
