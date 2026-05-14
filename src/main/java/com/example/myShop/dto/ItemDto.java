package com.example.myShop.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemDto {
    private String itemDetail;
    private String itemName;
    private int price;
    private LocalDateTime regTime;
}
