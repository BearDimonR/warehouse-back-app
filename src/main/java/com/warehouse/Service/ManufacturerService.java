package com.warehouse.Service;

import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.OrderBy;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Manufacturer;
import com.warehouse.Model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManufacturerService extends BasicService<Manufacturer> {
    public static ManufacturerService instance;

    public synchronized static ManufacturerService getInstance() {
        if (instance == null)
            instance = new ManufacturerService();
        return instance;
    }

    private ManufacturerService() {
        dao = ManufacturerDAO.getInstance();
    }

    @Override
    public Optional<Manufacturer> get(long id) throws SQLException {
        Optional<Manufacturer> manufacturer = dao.get(id);
        if(manufacturer.isPresent()) {
            Manufacturer g = manufacturer.get();
            g.setAmount(getManufacturerAmount(g.getId()).orElse(0f));
        }
        return manufacturer;
    }

    @Override
    public List<Manufacturer> getAll(Filter filter, PageFilter pageFilter, OrderBy order) throws SQLException {
        List<Manufacturer> manufacturers =  dao.getAll(filter, pageFilter, order);
        for (Manufacturer manufacturer: manufacturers) {
            manufacturer.setAmount(getManufacturerAmount(manufacturer.getId()).orElse(0f));
        }
        return manufacturers;
    }

    private Optional<Float> getManufacturerAmount(long id) throws SQLException {
        return ProductService.getInstance().getAllByManufacturer(id).stream().map(Product::getAmount).reduce(Float::sum);
    }

}
