package com.example.ecommercemarketplace.dto;

import com.example.ecommercemarketplace.models.enums.DeliveryMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDeliveryDataDto {

    private Long id;

    private AddressDto address;

    private DeliveryMethod method;
}
