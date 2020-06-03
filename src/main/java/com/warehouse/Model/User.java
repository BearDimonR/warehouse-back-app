package com.warehouse.Model;

import java.util.Objects;

public class User {

    private int id;
    private String name;
    private String password;
    private int roleId;

    public User(int id, String name, String password, int roleId ) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.roleId = roleId;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id &&
                Objects.equals(name, user.name) &&
                Objects.equals(password, user.password) &&
                roleId == user.roleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, roleId);
    }

    public String toString() {
        return "Permission { \nid = " + id +
                ", \nname = " + name +
                ", \npassword = " + password +
                ", \nrole_id = " + roleId +
                " \n}\n";
    }

}
