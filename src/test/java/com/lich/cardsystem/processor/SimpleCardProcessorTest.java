package com.lich.cardsystem.processor;

import com.lich.cardsystem.entities.Card;
import com.lich.cardsystem.entities.Transaction;
import com.lich.cardsystem.exceptions.CardException;
import com.lich.cardsystem.exceptions.CardNotFound;
import com.lich.cardsystem.exceptions.InsufficientFundsException;
import com.lich.cardsystem.exceptions.TransactionNotFound;
import com.lich.cardsystem.repositories.CardRepository;
import com.lich.cardsystem.repositories.TransactionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by leszek on 06/08/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleCardProcessorTest {

    private SimpleCardProcessor simpleCardProcessor;

    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private static final String CARD_ID = "cardId";
    private static final String TRANSACTION_ID = "transactionId";

    private Card card;
    private Card cardWithTransaction;

    @Before
    public void setUp() {
        simpleCardProcessor = new SimpleCardProcessor(cardRepository, transactionRepository);

        card = new Card(CARD_ID);
        card.setAvailableBalance(BigDecimal.TEN);
        card.setBlockedBalance(new BigDecimal(5));

        cardWithTransaction = new Card(CARD_ID);
        cardWithTransaction.setAvailableBalance(BigDecimal.TEN);
        cardWithTransaction.setBlockedBalance(new BigDecimal(5));
        cardWithTransaction.setTransactions(Arrays.asList(new Transaction(TRANSACTION_ID, cardWithTransaction, new BigDecimal(4))));
    }

    @Test
    public void shouldCreateCard() {
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card card = simpleCardProcessor.createCard(CARD_ID);

        verify(cardRepository, times(1)).save(isA(Card.class));
        Assert.assertEquals(CARD_ID, card.getId());
    }

    @Test
    public void shouldGetCard() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);

        Card card = simpleCardProcessor.getCard(CARD_ID);

        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertEquals(card, card);
    }

    @Test
    public void shouldCreditCard() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        Card card = simpleCardProcessor.credit(CARD_ID, BigDecimal.TEN);

        verify(cardRepository, times(1)).save(isA(Card.class));
        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertEquals(new BigDecimal(20), card.getAvailableBalance());
    }

    @Test(expected = CardNotFound.class)
    public void shouldThrowExceptionWhenCreditingCardThatDoesNotExist() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(null);

        Card card = simpleCardProcessor.credit(CARD_ID, BigDecimal.TEN);
    }

    @Test
    public void shouldAuthorize() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        simpleCardProcessor.authorize(CARD_ID, BigDecimal.ONE);

        verify(cardRepository, times(1)).save(isA(Card.class));
        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertEquals(new BigDecimal(9), card.getAvailableBalance());
        Assert.assertEquals(new BigDecimal(6), card.getBlockedBalance());
        Assert.assertEquals(1, card.getTransactions().size());
    }

    @Test(expected = InsufficientFundsException.class)
    public void shouldThrowInsufficientExceptionWhenAuthorizingMoreThanOnACard() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        simpleCardProcessor.authorize(CARD_ID, new BigDecimal(11));
    }

    @Test
    public void shouldCapture() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);

        Transaction transaction = simpleCardProcessor.capture(CARD_ID, TRANSACTION_ID);

        verify(cardRepository, times(1)).save(isA(Card.class));
        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertTrue(transaction.isCaptured());
    }

    @Test(expected = CardException.class)
    public void shouldThrowCardExceptionWhenTransactionAlreadyCaptured() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        simpleCardProcessor.capture(CARD_ID, TRANSACTION_ID);
    }

    @Test(expected = TransactionNotFound.class)
    public void shouldThrowTransactionNotFoundWhenTransactionMissing() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        simpleCardProcessor.capture(CARD_ID, "ASD");
    }

    @Test
    public void shouldReverse() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);

        Transaction transaction = simpleCardProcessor.reverse(CARD_ID, TRANSACTION_ID, BigDecimal.ONE);

        verify(cardRepository, times(1)).save(isA(Card.class));
        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertEquals(new BigDecimal(3), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(11), cardWithTransaction.getAvailableBalance());
        Assert.assertEquals(new BigDecimal(4), cardWithTransaction.getBlockedBalance());
    }

    @Test(expected = CardException.class)
    public void shouldThrowExceptionWhenTransactionAlreadyCaptured() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        simpleCardProcessor.reverse(CARD_ID, TRANSACTION_ID, BigDecimal.ONE);
    }

    @Test(expected = CardException.class)
    public void shouldThrowExceptionWhenReversingMoreThanPossible() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        simpleCardProcessor.reverse(CARD_ID, TRANSACTION_ID, new BigDecimal(5));
    }

    @Test
    public void shouldRefund() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);
        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        Transaction transaction = simpleCardProcessor.refund(CARD_ID, TRANSACTION_ID, BigDecimal.ONE);

        verify(cardRepository, times(1)).save(isA(Card.class));
        verify(cardRepository, times(1)).findOne(CARD_ID);
        Assert.assertEquals(new BigDecimal(3), transaction.getAmount());
        Assert.assertEquals(new BigDecimal(11), cardWithTransaction.getAvailableBalance());
        Assert.assertEquals(new BigDecimal(4), cardWithTransaction.getBlockedBalance());
    }

    @Test
    public void shouldThrowExceptionWhenTransactionNotCapturedYet() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);
        cardWithTransaction.getTransactions().get(0).setCaptured(false);

        try {
            simpleCardProcessor.refund(CARD_ID, TRANSACTION_ID, BigDecimal.ONE);
        } catch(CardException cardException) {
            if (!"Transaction not captured yet!".equals(cardException.getMessage())) {
                fail("Should throw exception - transaction not captured yet");
            }
        }
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRefundTooMuch() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);
        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        try {
            simpleCardProcessor.refund(CARD_ID, TRANSACTION_ID, BigDecimal.TEN);
        } catch(CardException cardException) {
            if (!"Cannot refund more that was authorized!".equals(cardException.getMessage())) {
                fail("Should throw exception - refund too big");
            }
        }
    }

    @Test
    public void shouldReturnTransactions() throws CardException {
        when(cardRepository.findOne(CARD_ID)).thenReturn(cardWithTransaction);
        when(cardRepository.save(any(Card.class))).thenReturn(cardWithTransaction);
        cardWithTransaction.getTransactions().get(0).setCaptured(true);

        List<Transaction> transactions = simpleCardProcessor.getTransactionHistory(CARD_ID);

        Assert.assertEquals(cardWithTransaction.getTransactions(), transactions);
    }
}