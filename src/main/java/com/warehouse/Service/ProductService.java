package com.warehouse.Service;

import com.warehouse.DAO.ProductDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Pair;
import com.warehouse.Model.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Product> getAllByGroup(long id) throws SQLException {
        List<Pair<String, String[]>> sId = new ArrayList<>(3);
        Pair<String, String[]> field = new Pair<>();
        field.key = "group_products_id";
        field.val = new String[]{String.valueOf(id)};
        sId.add(field);
        return getAll(Filter.builder().params(sId).build(), new PageFilter(), new OrderBy());
    }

    public List<Product> getAllByManufacturer(long id) throws SQLException {
        List<Pair<String, String[]>> sId = new ArrayList<>(3);
        Pair<String, String[]> field = new Pair<>();
        field.key = "manufacturer_id";
        field.val = new String[]{String.valueOf(id)};
        sId.add(field);
        return getAll(Filter.builder().params(sId).build(), new PageFilter(), new OrderBy());
    }
}
