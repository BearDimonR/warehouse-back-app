package com.warehouse.DAO;

import com.warehouse.Model.Permission;
import com.warehouse.Model.RolePermissionConnection;
import com.warehouse.Model.RolePermissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RolePermissionDAO {

    public static RolePermissionDAO instance;

    public synchronized static RolePermissionDAO getInstance() {
        if(instance == null)
            instance = new RolePermissionDAO();
        return instance;
    }

    private Connection connection;

    private RolePermissionDAO() {}

    public RolePermissions get(long id) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("SELECT * FROM permission WHERE id IN " +
                            "(SELECT permission_id FROM role_permission_connection WHERE role_id = ?)");
            preparedStatement.setLong(1, id);
            ResultSet res = preparedStatement.executeQuery();
            ArrayList<Permission> permissions = new ArrayList<>();
            while (res.next()) {
                permissions.add(new Permission(res.getLong(1),
                        res.getString(2),
                        res.getBoolean(3)));
            }
            return new RolePermissions(id, permissions);
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    public boolean save(RolePermissionConnection rolePermissionConnection) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement
                            ("INSERT INTO role_permission_connection (role_id, permission_id) VALUES (?,?)");
            preparedStatement.setLong(1, rolePermissionConnection.getRoleId());
            preparedStatement.setLong(2, rolePermissionConnection.getPermissionId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }

    public boolean delete(RolePermissionConnection rolePermissionConnection) throws SQLException {
        try {
            connection = DataBaseConnector.getConnector().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement
                    ("DELETE FROM role_permission_connection WHERE (role_id=? AND permission_id = ?) ");
            preparedStatement.setLong(1, rolePermissionConnection.getRoleId());
            preparedStatement.setLong(2, rolePermissionConnection.getPermissionId());
            return preparedStatement.executeUpdate() != 0;
        } finally {
            DataBaseConnector.getConnector().releaseConnection(connection);
            connection = null;
        }
    }
}
