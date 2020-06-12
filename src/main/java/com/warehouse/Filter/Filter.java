package com.warehouse.Filter;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class Filter {
    @Builder.Default
    boolean count = false;
    List<String> ids = null;
    Map<String, String> like = null;
    @NonNull
    @Builder.Default
    Integer page = 1;
    @NonNull
    @Builder.Default
    Integer size = 50;

    public String inKeys(String fieldname) {
        if(ids == null || ids.isEmpty())
            return null;
        return fieldname + " IN (" + String.join(", ", ids) + ")";
    }

    public String page() {
        return String.format("LIMIT %d OFFSET %d", size, (page-1) * size);
    }

    public String like() {
        if(like == null || like.isEmpty())
            return null;
        return like.keySet().stream()
                .map(x -> x += " LIKE '%" + like.get(x) + "%'").collect(Collectors.joining(" AND "));
    }
}
