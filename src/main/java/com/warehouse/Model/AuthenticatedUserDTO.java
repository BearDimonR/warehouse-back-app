package com.warehouse.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserDTO {
    private long id;
    private String name;
    private String token;
    private String expiration;
    private String role;
    private List<String> permissions;
}
