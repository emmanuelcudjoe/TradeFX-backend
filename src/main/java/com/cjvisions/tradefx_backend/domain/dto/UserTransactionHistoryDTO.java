package com.cjvisions.tradefx_backend.domain.dto;

import java.time.LocalDate;

public record UserTransactionHistoryDTO(
        String transactionId,
        String provider,
        String bankName,
        String transactionStatus,
        LocalDate date,
        Double requestedAmount
) {

}
