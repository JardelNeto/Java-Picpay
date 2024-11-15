package io.jardel.api.dto;

import java.time.LocalDateTime;

import io.jardel.model.Transaction;

public class PaymentResponse {

	public final String id;
	public final LocalDateTime timestamp;

	public PaymentResponse(Transaction transaction) {
		this.id = transaction.getId().toString();
		this.timestamp = transaction.getTimestamp();
	}

}
