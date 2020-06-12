package com.warehouse.Model;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor(staticName = "of")
public class ResponseMessage {
    @NonNull
    String message;
}
