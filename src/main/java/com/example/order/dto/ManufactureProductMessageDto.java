package com.example.order.dto;

import com.example.order.model.MeasureUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManufactureProductMessageDto {

    private UUID productId;

    private Integer amount;

    private MeasureUnit measureUnit;
}
