package com.easternpearl.ecommmerce.user.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStaticsDto {
   private Long allUsersCount;
   private Long allOrdersCount;
   private Long soldProductCount;
   private Double totalRevenue;
}
