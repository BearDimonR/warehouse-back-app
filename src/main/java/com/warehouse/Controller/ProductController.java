package com.warehouse.Controller;

import com.warehouse.Model.Product;
import com.warehouse.Service.ProductService;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ProductController extends AbstractController<Product> {

    public ProductController() {
        super(Product.class);
        viewPermissions = new ArrayList<>(Arrays.asList("product_page_view"));
        getPermission = "product_read";
        updatePermission = "product_edit";
        createPermission = "product_create";
        deletePermission = "product_delete";
        service = ProductService.getInstance();

        logger = LogManager.getLogger(ProductController.class);
    }
}
