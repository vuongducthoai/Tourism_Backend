package com.tourism.backend.dto.payos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOSResponse<T> {

    @JsonProperty("code")
    private String code;

    @JsonProperty("desc")
    private String desc;

    @JsonProperty("data")
    private T data;

    @JsonProperty("signature")
    private String signature;
}