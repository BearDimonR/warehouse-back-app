package com.warehouse.Filter;

import lombok.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class PageFilter {
    @NonNull
    @Builder.Default
    Integer page = 1;
    @NonNull
    @Builder.Default
    Integer size = 50;

    public String page() {
        return String.format("LIMIT %d OFFSET %d", size, (page-1) * size);
    }
}
