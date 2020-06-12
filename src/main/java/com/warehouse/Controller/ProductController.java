package com.warehouse.Controller;

import com.warehouse.DAO.ProductDAO;
import com.warehouse.Model.Product;
import com.warehouse.Service.ProductService;
import org.apache.logging.log4j.LogManager;

public class ProductController extends AbstractController<Product> {

    public ProductController() {
        super(Product.class);
        getPermission = "product_read";
        updatePermission = "product_edit";
        createPermission = "product_create";
        deletePermission = "product_delete";
        service = ProductService.getInstance();

        logger = LogManager.getLogger(ProductController.class);
    }

}
