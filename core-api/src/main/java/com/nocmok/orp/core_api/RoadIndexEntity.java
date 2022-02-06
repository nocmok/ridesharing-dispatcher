package com.nocmok.orp.core_api;

import java.util.Objects;

/**
 * Абстракция объекта на дорожном графе с которой работает дорожный индекс.
 * Сейчас используется только для хранения тс в индексе.
 */
public class RoadIndexEntity {

    /**
     * Идентификатор объекта в дорожном индексе.
     * Например: индекс тс
     */
    private final String id;

    /**
     * Координаты объекта в дорожном графе
     */
    private final GCS gcs;

    public RoadIndexEntity(String id, GCS gcs) {
        this.id = id;
        this.gcs = gcs;
    }

    public String getId() {
        return id;
    }

    public GCS getGcs() {
        return gcs;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoadIndexEntity that = (RoadIndexEntity) o;
        return id.equals(that.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }

    @Override public String toString() {
        return "RoadIndexEntity{" +
                "id='" + id + '\'' +
                ", gcs=" + gcs +
                '}';
    }
}
