package com.warehouse.Filter;

import lombok.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class PageFilter {
    @NonNull
    @Builder.Default
    Integer page = null;
    @NonNull
    @Builder.Default
    Integer size = null;

    public String page() {
        if(page != null && size != null)
            return String.format("LIMIT %d OFFSET %d", size, (page-1) * size);
        return "";
    }
}
