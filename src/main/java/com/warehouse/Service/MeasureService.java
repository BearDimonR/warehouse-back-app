package com.warehouse.Service;

import com.warehouse.DAO.MeasureDAO;
import com.warehouse.Model.Measure;

public class MeasureService extends BasicService<Measure> {
    public static MeasureService instance;

    public synchronized static MeasureService getInstance() {
        if (instance == null)
            instance = new MeasureService();
        return instance;
    }

    private MeasureService() {
        dao = MeasureDAO.getInstance();
    }
}
