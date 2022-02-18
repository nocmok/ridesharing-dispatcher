package com.nocmok.orp.orp_solver.job;

import lombok.Builder;

@Builder
public class OrpOutputMessage {
    private String partitionKey;
    private String messageKind;
    private String payload;

    public OrpOutputMessage(String partitionKey, String messageKind, String payload) {
        this.partitionKey = partitionKey;
        this.messageKind = messageKind;
        this.payload = payload;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    public String getMessageKind() {
        return messageKind;
    }

    public void setMessageKind(String messageKind) {
        this.messageKind = messageKind;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override public String toString() {
        return "OrpOutputMessage{" +
                "partitionKey='" + partitionKey + '\'' +
                ", messageKind='" + messageKind + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}
