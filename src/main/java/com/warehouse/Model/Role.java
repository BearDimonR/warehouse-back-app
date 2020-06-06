package com.warehouse.Model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private long id;
    private String name;
    private boolean is_super;
}
