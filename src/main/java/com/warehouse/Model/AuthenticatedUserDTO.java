package com.warehouse.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticatedUserDTO {
    private long id;
    private String name;
    private String token;
    private String renovationToken;
    private String expiration;
    private String renovationTokenExpiration;
    private long roleId;
    private boolean roleIsSuper;
    private List<String> permissions;
    private List<String> viewPermissions;
}
