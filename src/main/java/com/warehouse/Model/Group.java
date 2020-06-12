package com.warehouse.Model;


import lombok.*;


@Data
@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Builder
public class Group  {
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    private Float totalCost;
    private Float totalAmount;
}
