package com.nocmok.orp.api.storage.request_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadSegment {
    private String sourceId;
    private String targetId;
}
