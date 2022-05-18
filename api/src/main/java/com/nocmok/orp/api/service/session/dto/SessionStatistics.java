package com.nocmok.orp.api.service.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class SessionStatistics {
    private Double distanceTravelled;
}
