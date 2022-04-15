package com.nocmok.orp.api.service.session_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SessionDto {

    private String sessionId;
    private Integer initialCapacity;
    private Double initialLatitude;
    private Double initialLongitude;
    private String sourceId;
    private String targetId;
    private Instant createdAt;

}
