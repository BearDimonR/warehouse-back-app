package com.warehouse.DAO;

import com.warehouse.Model.RolePermissionConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RolePermissionDAO {

    public static RolePermissionDAO instance;

    public synchronized static RolePermissionDAO getInstance() {
        if(instance == null)
            instance = new RolePermissionDAO();
        return instance;
    }

    private RolePermissionDAO() {}

    public List<Long> get(long id) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT permission_id FROM role_permission_connection WHERE role_id = ?");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            ArrayList<Long> permissions = new ArrayList<>();
            while (res.next()) {
                permissions.add(res.getLong(1));
            }
            return permissions;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    public long create(RolePermissionConnection rolePermissionConnection) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement
                            ("INSERT INTO role_permission_connection (role_id, permission_id) VALUES (?,?) RETURNING role_id");
            preparedStatement.setLong(1, rolePermissionConnection.getRoleId());
            preparedStatement.setLong(2, rolePermissionConnection.getPermissionId());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getLong(1);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }

    public boolean delete(RolePermissionConnection rolePermissionConnection) throws SQLException {
        Connection connection = DataBaseConnector.getConnector().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("DELETE FROM role_permission_connection WHERE (role_id=? AND permission_id = ?) ");
            preparedStatement.setLong(1, rolePermissionConnection.getRoleId());
            preparedStatement.setLong(2, rolePermissionConnection.getPermissionId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
        }
    }
}
