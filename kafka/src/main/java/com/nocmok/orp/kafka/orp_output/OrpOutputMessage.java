package com.nocmok.orp.kafka.orp_output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Универсальный формат сообщения, используемый в топике orp.output
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrpOutputMessage {
    private String partitionKey;
    private String messageKind;
    private String payload;
}
