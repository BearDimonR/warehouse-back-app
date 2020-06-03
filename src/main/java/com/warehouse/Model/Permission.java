package com.warehouse.Model;

import java.util.Objects;

public class Permission {
    private int id;
    private String name;
    private boolean isSuper;

    public Permission(int id, String name, boolean isSuper) {
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

    public boolean isIsSuper() {
        return isSuper;
    }

    public void setIsSuper(boolean is_super) {
        this.isSuper = is_super;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Permission permission = (Permission) obj;
        return id == permission.id &&
                Objects.equals(name, permission.name) &&
                isSuper == permission.isSuper;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isSuper);
    }

    public String toString() {
        return "Permission { \nid = " + id +
                ", \nname = " + name +
                ", \nis_super = " + isSuper +
                " }\n";
    }

}
