package com.warehouse.Model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor(staticName = "of")
public class ResponseMessage {
    @NonNull
    String message;
}
