package com.warehouse.Model.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthenticatedUserDTO {
    private long id;
    private String name;
    private String token;
    private String role;
    private List<String> permissions;
}
