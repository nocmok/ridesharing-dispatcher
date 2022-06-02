package com.nocmok.orp.solver.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleEntry {

    /**
     * Крайнее время прибытия в контрольную точку
     */
    @JsonProperty("deadline")
    private Instant deadline;
    /**
     * Дельта нагрузки на тс при прохождении контрольной точки
     */
    @JsonProperty("load")
    private Integer load;
    /**
     * Идентификатор вершины в графе к которой прикреплена контрольная точка
     */
    @JsonProperty("nodeId")
    private String nodeId;
    /**
     * Широта координаты контрольной точки
     */
    @JsonProperty("latitude")
    private Double latitude;
    /**
     * Долгота координаты контрольной точки
     */
    @JsonProperty("longitude")
    private Double longitude;
    /**
     * Тип контрольной точки
     */
    @JsonProperty("kind")
    private ScheduleEntryKind kind;
    /**
     * Идентификатор заказа прикрепленного к контрольной точке
     */
    @JsonProperty("orderId")
    private String orderId;


    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScheduleEntry that = (ScheduleEntry) o;
        return Objects.equals(nodeId, that.nodeId) && kind == that.kind && Objects.equals(orderId, that.orderId);
    }

    @Override public int hashCode() {
        return Objects.hash(nodeId, kind, orderId);
    }
}
