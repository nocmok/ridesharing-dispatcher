package com.nocmok.orp.postgres.storage.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public class OrpOutputOutboxRecord<T> {
    /**
     * Идентификатор сообщения
     */
    private Long messageId;
    /**
     * Ключ по которому сообщение попадет в партицию топика кафки
     */
    private String partitionKey;
    /**
     * Полезная часть сообщения
     */
    private T payload;
    /**
     * Тип сообщения
     */
    private String messageKind;
    /**
     * Время создания сообщения. Для сохранения порядка сообщений
     */
    private Instant createdAt;
    /**
     * Время отправки сообщения.
     * null, если сообщение не было отправлено
     */
    private Instant sentAt;

    public OrpOutputOutboxRecord(Long messageId, String partitionKey, T payload, String messageKind, Instant createdAt, Instant sentAt) {
        this.messageId = messageId;
        this.partitionKey = partitionKey;
        this.payload = payload;
        this.messageKind = messageKind;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getMessageKind() {
        return messageKind;
    }

    public void setMessageKind(String messageKind) {
        this.messageKind = messageKind;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    @Override public String toString() {
        return "OrpOutputOutboxRecord{" +
                "messageId=" + messageId +
                ", partitionKey='" + partitionKey + '\'' +
                ", payload=" + payload +
                ", messageKind='" + messageKind + '\'' +
                ", createdAt=" + createdAt +
                ", sentAt=" + sentAt +
                '}';
    }
}
