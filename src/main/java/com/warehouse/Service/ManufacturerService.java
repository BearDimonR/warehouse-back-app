package com.warehouse.Service;

import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.Model.Manufacturer;

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
}
