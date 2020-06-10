package com.warehouse.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.warehouse.DAO.MeasureDAO;
import com.warehouse.JsonProceed;
import com.warehouse.Model.Measure;
import com.warehouse.Model.Role;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;

public class MeasureHandler extends AbstractHandler {

    public MeasureHandler() {
        getPermission = "measure_read";
        updatePermission = "";
        createPermission = "";
        deletePermission = "";

        logger = LogManager.getLogger(MeasureHandler.class);
        model = Role.class;
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException, SQLException, InvalidParameterException {
        List<Measure> measures = MeasureDAO.getInstance().getAll();
        String json = JsonProceed.getGson().toJson(measures);
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        //TODO Encrypt measures
        os.write(json.getBytes());
        os.flush();
    }

}
