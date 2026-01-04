package com.example.appmenu.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommandeEnrichieDto(
        Long id,
        int tableNum,
        List<ItemCommandeEnrichiDto> items,
        String status,
        LocalDateTime createdAt,
        Double total
) {}