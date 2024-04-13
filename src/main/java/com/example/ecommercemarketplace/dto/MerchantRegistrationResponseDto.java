package com.example.ecommercemarketplace.dto;

import com.example.ecommercemarketplace.models.enums.MerchantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MerchantRegistrationResponseDto extends UserRegistrationResponseDto {

    private MerchantType type;
}
