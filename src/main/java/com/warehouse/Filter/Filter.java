package com.warehouse.Filter;

import com.warehouse.Model.Pair;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Filter {
    @Builder.Default
    boolean count = false;
    @Builder.Default
    List<Pair<String, String[]>> params = null;
    @Builder.Default
    List<Long> ids = null;
    @Builder.Default
    List<Pair<String, String>> like = null;


    public String inKeys(String defaultField) {
        if(!(ids == null || ids.isEmpty()))
            return defaultField + " IN (" + ids.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")";
        else if(!(params == null || params.isEmpty())) {
             return params.stream()
                     .map(x -> x.key += " IN (" + String.join(", ",  x.val) + ")")
                     .collect(Collectors.joining(" AND "));
        } else
            return null;
    }


    public String like() {
        if(like == null || like.isEmpty())
            return null;
        return like.stream()
                .map(x -> x.key += " LIKE '%" + x.val + "%'").collect(Collectors.joining(" AND "));
    }
}
