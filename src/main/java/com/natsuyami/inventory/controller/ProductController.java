package com.natsuyami.inventory.controller;

import com.natsuyami.inventory.dto.ProductDetailsDto;
import com.natsuyami.inventory.dto.ProductDto;
import com.natsuyami.inventory.factory.ProductFactory;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(value = "/product", produces = "application/json")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductFactory productService;

    @ApiOperation(value = "get all products or specific category of products",
            notes = "no error format as of now, pageable has different attribute name documented in swagger",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "there is an invalid data from database")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "offset", dataType = "integer", paramType = "query",
                    value = "offset of the page you want to retrieve (0..N) does not rely on page"),
            @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")
    })
    @GetMapping(value = {"", "/{category}"})
    public Object getAll(@PathVariable(value = "category", required = false) String category,
                         @ApiIgnore("Ignored because swagger ui shows the wrong params, " +
                                 "instead they are explained in the implicit params")
                         @SortDefault.SortDefaults({
                                 @SortDefault(sort = "productName", direction = Sort.Direction.ASC),
                                 @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                         }) Pageable pageable) {
        LOGGER.info("Initialized ProductController getAll() method for category={{}}, page={{}}. size={{}}", category, pageable.getPageNumber(), pageable.getPageSize());
        List<ProductDetailsDto> products = productService.getProduct(category).getAll(pageable);
        return products;
    }

    @ApiOperation(value = "search a products with the given filter",
            notes = "no error format as of now",
            response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 500, message = "there is an invalid data from database")
    })
    @GetMapping(value = {"/search", "/search/{category}"})
    public Object search(@PathVariable(value = "category", required = false) String category,
                         @RequestParam("search") String search) {
        LOGGER.info("Initialized ProductController search() method for category={{}} with search={{}}", category, search);
        List<ProductDto> products = productService.getProduct(category).search(search);
        return products;
    }
}
