package ru.otus.bank.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.bank.dao.AccountDao;
import ru.otus.bank.entity.Account;
import ru.otus.bank.entity.Agreement;
import ru.otus.bank.service.exception.AccountException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final BigDecimal INITIAL_AMOUNT = BigDecimal.valueOf(10);

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
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(SOURCE_ACCOUNT_ID);

        destinationAccount = new Account();
        destinationAccount.setAmount(INITIAL_AMOUNT);
        destinationAccount.setId(DESTINATION_ACCOUNT_ID);
    }

    @Test
    public void shouldTransferAmountBetweenAccounts() {
        when(accountDao.findById(eq(SOURCE_ACCOUNT_ID))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(DESTINATION_ACCOUNT_ID))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(SOURCE_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, INITIAL_AMOUNT);

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class,
                () -> accountServiceImpl.makeTransfer(SOURCE_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, INITIAL_AMOUNT));

        assertEquals("No source account", result.getLocalizedMessage());
    }

    @Test
    public void testTransferWithVerify() {
        when(accountDao.findById(eq(SOURCE_ACCOUNT_ID))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(DESTINATION_ACCOUNT_ID))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(SOURCE_ACCOUNT_ID) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(DESTINATION_ACCOUNT_ID) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(SOURCE_ACCOUNT_ID, DESTINATION_ACCOUNT_ID, INITIAL_AMOUNT);

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }

    @Test
    public void testGetAccounts() {
        HashMap<Long, Account> accountMap = new HashMap<>();

        Account account1 = new Account();
        account1.setId(SOURCE_ACCOUNT_ID);

        Account account2 = new Account();
        account2.setId(DESTINATION_ACCOUNT_ID);

        List<Account> expectedResult = List.of(account1, account2);

        accountMap.put(SOURCE_ACCOUNT_ID, account1);
        accountMap.put(DESTINATION_ACCOUNT_ID, account2);

        when(accountDao.findAll()).thenReturn(accountMap.values());

        List<Account> actualResult = accountServiceImpl.getAccounts();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetAccountsById() {
        Long agreementId = SOURCE_ACCOUNT_ID;
        Agreement agreement = new Agreement();
        agreement.setId(agreementId);

        HashMap<Long, Account> accountMap = new HashMap<>();

        Account account1 = new Account();
        account1.setId(SOURCE_ACCOUNT_ID);
        account1.setAgreementId(SOURCE_ACCOUNT_ID);

        Account account2 = new Account();
        account2.setId(DESTINATION_ACCOUNT_ID);
        account2.setAgreementId(DESTINATION_ACCOUNT_ID);

        List<Account> expectedResult = List.of(account1);

        accountMap.put(SOURCE_ACCOUNT_ID, account1);
        accountMap.put(DESTINATION_ACCOUNT_ID, account2);

        when(accountDao.findByAgreementId(agreementId)).thenReturn(accountMap.values().stream()
                .filter(account -> account.getAgreementId().equals(agreementId))
                .toList());

        List<Account> actualResult = accountServiceImpl.getAccounts(agreement);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCharge() {
        Account account = new Account();
        account.setAmount(INITIAL_AMOUNT);

        when(accountDao.findById(any())).thenReturn(Optional.of(account));
        when(accountDao.save(any())).thenReturn(account);

        boolean result = accountServiceImpl.charge(SOURCE_ACCOUNT_ID, INITIAL_AMOUNT);

        assertTrue(result);
    }

    @Test
    public void testChargeThrows() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountServiceImpl.charge(SOURCE_ACCOUNT_ID, INITIAL_AMOUNT));
    }

}