package com.warehouse.Model;

import java.util.Objects;

public class Group {

    private int id;
    private String name;
    private String description;

    public Group(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Group group = (Group) obj;
        return id == group.id &&
                Objects.equals(name, group.name) &&
                description == group.description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    public String toString() {
        return "Permission { \nid = " + id +
                ", \nname = " + name +
                ", \ndescription = " + description +
                " \n}\n";
    }
}
