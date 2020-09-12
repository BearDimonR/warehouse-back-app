package com.warehouse.Model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {
    @NonNull
    private long id;
    @NonNull
    private String name;
    @NonNull
    private boolean isSuper;

    private Integer userNumber = null;
}
