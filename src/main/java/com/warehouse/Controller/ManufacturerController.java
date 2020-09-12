package com.warehouse.Controller;

import com.warehouse.Model.Manufacturer;
import com.warehouse.Service.ManufacturerService;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ManufacturerController extends AbstractController<Manufacturer> {

    public ManufacturerController() {
        super(Manufacturer.class);
        viewPermissions = new ArrayList<>(Arrays.asList("manufacturer_page_view","product_page_view"));
        getPermission = "manufacturer_read";
        updatePermission = "manufacturer_edit";
        createPermission = "manufacturer_create";
        deletePermission = "manufacturer_edit";
        service = ManufacturerService.getInstance();

        logger = LogManager.getLogger(ManufacturerController.class);
    }
}
