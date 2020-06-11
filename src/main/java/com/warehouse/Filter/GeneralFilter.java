package com.warehouse.Filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Builder
public class GeneralFilter implements Filter{
    List<Integer> ids;

    @Override
    public String inIds(String fieldname) {
        if(ids == null || ids.isEmpty())
            return null;
        return fieldname + " IN (" + ids.stream().map(Objects::toString).collect(Collectors.joining(", ")) + ")";
    }

}
