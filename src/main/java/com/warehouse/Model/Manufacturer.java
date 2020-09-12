package com.warehouse.Model;

import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class Manufacturer {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    private Float amount;
}
