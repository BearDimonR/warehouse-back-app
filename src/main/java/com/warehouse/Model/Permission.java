package com.warehouse.Model;


import lombok.*;

@Data
@AllArgsConstructor
@Builder
@RequiredArgsConstructor(staticName = "of")
public class Permission {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private Boolean isSuper;

}
