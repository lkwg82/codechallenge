package de.lgohlke.codedemo.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Transaction {
    private final double amount;
    private final long timestamp;

    @JsonCreator
    Transaction(@JsonProperty("amount") double amount,
                @JsonProperty("timestamp") long timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }
}
