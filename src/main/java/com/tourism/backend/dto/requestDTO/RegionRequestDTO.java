package com.tourism.backend.dto.requestDTO;

import com.tourism.backend.enums.Region;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDTO {

    // Tên trường phải khớp với payload từ Frontend { region: "NORTH" }
    @NotNull(message = "Region must be specified")
    private Region region;
}