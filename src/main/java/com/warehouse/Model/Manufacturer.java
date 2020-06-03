package com.warehouse.Model;

import java.util.Objects;

public class Manufacturer {

    private int id;
    private String name;

    public Manufacturer(int id, String name){
        this.id = id;
        this.name = name;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Manufacturer manufacturer = (Manufacturer) obj;
        return id == manufacturer.id &&
                Objects.equals(name, manufacturer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public String toString() {
        return "Permission { \nid = " + id +
                ", \nname = " + name +
                " \n}\n";
    }

}
