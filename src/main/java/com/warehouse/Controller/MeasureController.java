package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Model.Measure;
import com.warehouse.Service.MeasureService;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;

public class MeasureController extends AbstractController<Measure> {

    public MeasureController() {
        super(Measure.class);
        viewPermissions = new ArrayList<>(Arrays.asList("product_page_view","product_page_view"));
        getPermission = "measure_read";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";
        service = MeasureService.getInstance();

        logger = LogManager.getLogger(MeasureController.class);
    }

    @Override
    protected Object create(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object update(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

    @Override
    protected Object delete(HttpExchange exchange) throws NotImplementedException {
        throw new NotImplementedException();
    }

}
