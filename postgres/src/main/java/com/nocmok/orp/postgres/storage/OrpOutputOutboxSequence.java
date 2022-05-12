package com.nocmok.orp.postgres.storage;

import org.springframework.stereotype.Component;

@Component
public class OrpOutputOutboxSequence {

    public Long nextValue() {
        throw new UnsupportedOperationException("not implemented");
    }
}
