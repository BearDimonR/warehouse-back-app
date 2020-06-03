package com.warehouse.Model;

import java.util.Objects;

public class Role {

    private int id;
    private String name;
    private boolean isSuper;

    public Role(int id, String name, boolean isSuper) {
        this.id = id;
        this.name = name;
        this.isSuper = isSuper;
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

    public boolean isSuper() {
        return isSuper;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return id == role.id &&
                Objects.equals(name, role.name) &&
                isSuper == role.isSuper;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isSuper);
    }

    public String toString() {
        return "Permission { \nid = " + id +
                ", \nname = " + name +
                ", \nis_super = " + isSuper +
                " \n}\n";
    }

}
