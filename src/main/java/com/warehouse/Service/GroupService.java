package com.warehouse.Service;

import com.warehouse.DAO.GroupDAO;
import com.warehouse.Filter.Filter;
import com.warehouse.Filter.PageFilter;
import com.warehouse.Model.Group;
import com.warehouse.Model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GroupService extends BasicService<Group> {
    public static GroupService instance;

    public synchronized static GroupService getInstance() {
        if (instance == null)
            instance = new GroupService();
        return instance;
    }

    private GroupService() {
        dao = GroupDAO.getInstance();
    }

    @Override
    public Optional<Group> get(long id) throws SQLException {
        Optional<Group> group = dao.get(id);
        if(group.isPresent()) {
            Group g = group.get();
            g.setTotalAmount(getGroupTotalAmount(id).orElse(0f));
            g.setTotalCost(getGroupTotalAmount(id).orElse(0f));
        }
        return group;    }

    @Override
    public List<Group> getAll(Filter filter, PageFilter pageFilter) throws SQLException {
        List<Group> groups =  dao.getAll(filter, pageFilter);
        Long id;
        for (Group group: groups) {
            id = group.getId();
            group.setTotalCost(getGroupTotalCost(id).orElse(0f));
            group.setTotalAmount(getGroupTotalAmount(id).orElse(0f));
        }
        return groups;
    }

    @Override
    public long count(Filter filter) throws SQLException {
        return dao.getAll(filter, new PageFilter()).size();
    }

    private Optional<Float> getGroupTotalCost(long id) throws SQLException {
        return ProductService.getInstance().getAllByGroup(id).stream().map(Product::getTotalCost).reduce(Float::sum);
    }

    private Optional<Float> getGroupTotalAmount(long id) throws SQLException {
        return ProductService.getInstance().getAllByGroup(id).stream().map(Product::getAmount).reduce(Float::sum);
    }

}
