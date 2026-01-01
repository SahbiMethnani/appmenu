package com.example.appmenu.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommandeDto(
        Long id,
        int tableNum,
        List<ItemCommandeDto> items,
        String status,
        LocalDateTime createdAt,
        Double total
) {}