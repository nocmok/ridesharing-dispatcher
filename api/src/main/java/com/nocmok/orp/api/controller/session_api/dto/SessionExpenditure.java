package com.nocmok.orp.api.controller.session_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SessionExpenditure {

    private Double distanceTravelled;
}
