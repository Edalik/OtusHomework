package ru.otus.bank.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import ru.otus.bank.dao.AgreementDao;
import ru.otus.bank.entity.Agreement;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

class AgreementServiceImplTest {

    private static final Long ID = 10L;

    private final AgreementDao dao = Mockito.mock(AgreementDao.class);

    AgreementServiceImpl agreementServiceImpl;

    String name;

    Agreement agreement;

    @BeforeEach
    public void setUp() {
        agreementServiceImpl = new AgreementServiceImpl(dao);

        name = "test";
        agreement = new Agreement();
        agreement.setId(ID);
        agreement.setName(name);
    }

    @Test
    public void testFindByName() {

        Mockito.when(dao.findByName(name)).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(ID, agreement.getId());
    }

    @Test
    public void testFindByNameWithCaptor() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.when(dao.findByName(captor.capture())).thenReturn(
                Optional.of(agreement));

        Optional<Agreement> result = agreementServiceImpl.findByName(name);

        Assertions.assertEquals(name, captor.getValue());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(ID, agreement.getId());
    }

    @Test
    public void testAddAgreement() {
        ArgumentMatcher<Agreement> agreement = argument -> argument.getName().equals(name);

        agreementServiceImpl.addAgreement(name);

        verify(dao).save(argThat(agreement));
    }

}