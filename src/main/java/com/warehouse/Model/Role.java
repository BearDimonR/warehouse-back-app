package com.warehouse.Model;

public class Role {
    long id;
    String name;
    boolean is_super;

    public Role() {}

    public Role(long id, String name, boolean is_super) {
        this.id = id;
        this.name = name;
        this.is_super = is_super;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIs_super() {
        return is_super;
    }

    public void setIs_super(boolean is_super) {
        this.is_super = is_super;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", is_super=" + is_super +
                '}';
    }
}
