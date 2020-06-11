package com.warehouse.Controller;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.MeasureDAO;
import com.warehouse.Exception.NotImplementedException;
import com.warehouse.Model.Measure;
import org.apache.logging.log4j.LogManager;

public class MeasureController extends AbstractController<Measure> {

    public MeasureController() {
        super(Measure.class);
        getPermission = "measure_read";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";
        dao = MeasureDAO.getInstance();

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
