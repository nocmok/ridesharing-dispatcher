package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class GetSessionExpendituresResponse {

    @Singular("expenditure")
    @JsonProperty("expenditures")
    private Map<String, SessionExpenditure> expenditures;

}
