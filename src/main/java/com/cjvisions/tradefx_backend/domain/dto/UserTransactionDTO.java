package com.cjvisions.tradefx_backend.domain.dto;

public record UserTransactionDTO(
        String userEmail,
        String buyingCurrency,
        String sellingCurrency,
        String accountNumber,
        String accountName,
        String branchName,
        String contact,
        String providerName,
        Long amount,
        String bank
        )
{
}
