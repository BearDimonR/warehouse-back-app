package com.warehouse.Filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderBy {
    @Builder.Default
    List<String> fields = null;
    @Builder.Default
    boolean isAscending = true;

    public String orderBy(String def) {
        if (fields == null || fields.isEmpty())
            return String.format("ORDER BY %s %s", def, order());
        return String.format("ORDER BY %s %s", String.join(", ", fields), order());
    }

    private String order() {
        return isAscending ? "ASC" : "DESC";
    }
}
