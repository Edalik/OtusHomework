package ru.otus.bank.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplParametrizedTest {

    private static final Long SOURCE_ACCOUNT_ID = 1L;
    private static final Long DESTINATION_ACCOUNT_ID = 2L;

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    Account sourceAccount;

    Account destinationAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = new Account();
        sourceAccount.setId(SOURCE_ACCOUNT_ID);

        destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(DESTINATION_ACCOUNT_ID);
    }

    @ParameterizedTest
    @CsvSource({"100, 10, true", "10, 100, false", "10, 0, false", "10, -1, false"})
    public void testTransferValidation(String sourceSum, String transferSum, String expectedResult) {
        BigDecimal sourceAmount = new BigDecimal(sourceSum);
        BigDecimal transferAmount = new BigDecimal(transferSum);
        Boolean expected = Boolean.parseBoolean(expectedResult);

        sourceAccount.setAmount(sourceAmount);

        when(accountDao.findById(eq(SOURCE_ACCOUNT_ID))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(DESTINATION_ACCOUNT_ID))).thenReturn(Optional.of(destinationAccount));

        assertEquals(expected, accountServiceImpl.makeTransfer(SOURCE_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, transferAmount));
    }

    @ParameterizedTest
    @MethodSource("provideParameters")
    public void testTransferValidationMethodSource(BigDecimal sourceAmount, BigDecimal transferAmount, Boolean expected) {
        sourceAccount.setAmount(sourceAmount);

        when(accountDao.findById(eq(SOURCE_ACCOUNT_ID))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(DESTINATION_ACCOUNT_ID))).thenReturn(Optional.of(destinationAccount));

        assertEquals(expected, accountServiceImpl.makeTransfer(SOURCE_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, transferAmount));
    }

    public static Stream<? extends Arguments> provideParameters() {
        return Stream.of(
                Arguments.of(new BigDecimal(100), new BigDecimal(10), true),
                Arguments.of(new BigDecimal(10), new BigDecimal(100), false),
                Arguments.of(new BigDecimal(100), new BigDecimal(0), false),
                Arguments.of(new BigDecimal(100), new BigDecimal(-1), false)
        );
    }

    @ParameterizedTest
    @CsvSource("1, 1, 1, 1")
    public void testAddAccount(Long agreementId, String accountNumber, Integer type, BigDecimal amount) {
        Agreement agreement = new Agreement();
        agreement.setId(agreementId);

        ArgumentMatcher<Account> account = argument -> argument.getAgreementId().equals(agreementId)
                && argument.getNumber().equals(accountNumber)
                && argument.getType().equals(type)
                && argument.getAmount().equals(amount);

        accountServiceImpl.addAccount(agreement, accountNumber, type, amount);

        verify(accountDao).save(argThat(account));
    }

}