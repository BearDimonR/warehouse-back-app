package com.warehouse.Controller;

import com.warehouse.DAO.ManufacturerDAO;
import com.warehouse.Model.Manufacturer;
import org.apache.logging.log4j.LogManager;

public class ManufacturerController extends AbstractController<Manufacturer> {

    public ManufacturerController() {
        super(Manufacturer.class);
        getPermission = "manufacturer_read";
        updatePermission = "manufacturer_edit";
        createPermission = "manufacturer_create";
        deletePermission = "manufacturer_edit";
        dao = ManufacturerDAO.getInstance();

        logger = LogManager.getLogger(ManufacturerController.class);
    }
}
