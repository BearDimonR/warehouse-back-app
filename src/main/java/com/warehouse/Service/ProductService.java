package com.warehouse.Service;

import com.warehouse.DAO.ProductDAO;
import com.warehouse.Model.Product;

public class ProductService extends BasicService<Product> {
    public static ProductService instance;

    public synchronized static ProductService getInstance() {
        if (instance == null)
            instance = new ProductService();
        return instance;
    }

    private ProductService() {
        dao = ProductDAO.getInstance();
    }
}
