package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
public class AccountServiceImplTest {

    @Mock
    AccountDao accountDao;

    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Test
    public void testTransfer() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        assertEquals(new BigDecimal(90), sourceAccount.getAmount());
        assertEquals(new BigDecimal(20), destinationAccount.getAmount());
    }

    @Test
    public void testSourceNotFound() {
        when(accountDao.findById(any())).thenReturn(Optional.empty());

        AccountException result = assertThrows(AccountException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));
            }
        });
        assertEquals("No source account", result.getLocalizedMessage());
    }

    @Test
    public void testTransferWithVerify() {
        Account sourceAccount = new Account();
        sourceAccount.setAmount(new BigDecimal(100));
        sourceAccount.setId(1L);

        Account destinationAccount = new Account();
        destinationAccount.setAmount(new BigDecimal(10));
        destinationAccount.setId(2L);

        when(accountDao.findById(eq(1L))).thenReturn(Optional.of(sourceAccount));
        when(accountDao.findById(eq(2L))).thenReturn(Optional.of(destinationAccount));

        ArgumentMatcher<Account> sourceMatcher =
                argument -> argument.getId().equals(1L) && argument.getAmount().equals(new BigDecimal(90));

        ArgumentMatcher<Account> destinationMatcher =
                argument -> argument.getId().equals(2L) && argument.getAmount().equals(new BigDecimal(20));

        accountServiceImpl.makeTransfer(1L, 2L, new BigDecimal(10));

        verify(accountDao).save(argThat(sourceMatcher));
        verify(accountDao).save(argThat(destinationMatcher));
    }

    @Test
    public void testGetAccounts() {
        HashMap<Long, Account> accountMap = new HashMap<>();

        Account account1 = new Account();
        account1.setId(1L);

        Account account2 = new Account();
        account2.setId(2L);

        List<Account> expectedResult = List.of(account1, account2);

        accountMap.put(1L, account1);
        accountMap.put(2L, account2);

        when(accountDao.findAll()).thenReturn(accountMap.values());

        List<Account> actualResult = accountServiceImpl.getAccounts();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetAccountsById() {
        Long agreementId = 1L;
        Agreement agreement = new Agreement();
        agreement.setId(agreementId);

        HashMap<Long, Account> accountMap = new HashMap<>();

        Account account1 = new Account();
        account1.setId(1L);
        account1.setAgreementId(1L);

        Account account2 = new Account();
        account2.setId(2L);
        account2.setAgreementId(2L);

        List<Account> expectedResult = List.of(account1);

        accountMap.put(1L, account1);
        accountMap.put(2L, account2);

        when(accountDao.findByAgreementId(agreementId)).thenReturn(accountMap.values().stream()
                .filter(account -> account.getAgreementId().equals(agreementId))
                .toList());

        List<Account> actualResult = accountServiceImpl.getAccounts(agreement);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCharge() {
        Long accountId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(10);

        Account account = new Account();
        account.setAmount(chargeAmount);

        when(accountDao.findById(any())).thenReturn(Optional.of(account));
        when(accountDao.save(any())).thenReturn(account);

        boolean result = accountServiceImpl.charge(accountId, chargeAmount);

        assertTrue(result);
    }

    @Test
    public void testChargeThrows() {
        Long accountId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(10);

        when(accountDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountServiceImpl.charge(accountId, chargeAmount));
    }

}