package ru.otus.bank.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.AccountService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorImplTest {

    private static final Long SOURCE_ID = 1L;
    private static final Long DESTINATION_ID = 2L;

    @Mock
    AccountService accountService;

    @InjectMocks
    PaymentProcessorImpl paymentProcessor;

    Agreement sourceAgreement;

    Agreement destinationAgreement;

    Account sourceAccount;

    Account destinationAccount;

    @BeforeEach
    void setUp() {
        sourceAgreement = new Agreement();
        sourceAgreement.setId(SOURCE_ID);

        destinationAgreement = new Agreement();
        destinationAgreement.setId(DESTINATION_ID);

        sourceAccount = new Account();
        sourceAccount.setAmount(BigDecimal.valueOf(200));
        sourceAccount.setType(0);
        sourceAccount.setId(SOURCE_ID);

        destinationAccount = new Account();
        destinationAccount.setAmount(BigDecimal.valueOf(0));
        destinationAccount.setType(0);
        destinationAccount.setId(DESTINATION_ID);
    }

    @Test
    public void testTransfer() {
        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == SOURCE_ID))).thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument ->
                argument != null && argument.getId() == DESTINATION_ID))).thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransfer(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.ONE);

    }

    @Test
    public void testTransferWithCommission() {
        when(accountService.getAccounts(argThat(argument -> argument != null && argument.getId() == SOURCE_ID)))
                .thenReturn(List.of(sourceAccount));

        when(accountService.getAccounts(argThat(argument -> argument != null && argument.getId() == DESTINATION_ID)))
                .thenReturn(List.of(destinationAccount));

        paymentProcessor.makeTransferWithComission(sourceAgreement, destinationAgreement,
                0, 0, BigDecimal.valueOf(100), BigDecimal.valueOf(0.1));

        verify(accountService).charge(argThat(id -> id.equals(SOURCE_ID)), argThat(amount -> amount.equals(BigDecimal.valueOf(-10.0))));
    }

}