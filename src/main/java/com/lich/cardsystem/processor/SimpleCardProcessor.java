package com.lich.cardsystem.processor;

import com.lich.cardsystem.entities.Card;
import com.lich.cardsystem.entities.Transaction;
import com.lich.cardsystem.exceptions.CardException;
import com.lich.cardsystem.exceptions.CardNotFound;
import com.lich.cardsystem.exceptions.InsufficientFundsException;
import com.lich.cardsystem.exceptions.TransactionNotFound;
import com.lich.cardsystem.repositories.CardRepository;
import com.lich.cardsystem.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Created by leszek on 05/08/18.
 */
@Service
public class SimpleCardProcessor implements CardProcessor {

    @Autowired
    public SimpleCardProcessor(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    private CardRepository cardRepository;
    private TransactionRepository transactionRepository;

    @Override
    public String authorize(String cardId, BigDecimal amount) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        String transactionId = generateTransactionId();
        if (amount.compareTo(card.getAvailableBalance()) <= 0) {
            card.setAvailableBalance(card.getAvailableBalance().subtract(amount));
            card.setBlockedBalance(card.getBlockedBalance().add(amount));
            card.getTransactions().add(new Transaction(transactionId, card, amount));
        } else {
            throw new InsufficientFundsException();
        }

        cardRepository.save(card);

        return transactionId;
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString().substring(0, 5);
    }

    @Override
    public Transaction capture(String cardId, String transactionId) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        Transaction transaction = card.getTransactions().stream().filter(t -> t.getId().equals(transactionId)).findFirst().orElse(null);
        if (transaction == null) {
            throw new TransactionNotFound();
        }

        if (transaction.isCaptured()) {
            throw new CardException("Transaction already captured!");
        }

        transaction.setCaptured(true);

        cardRepository.save(card);

        return transaction;
    }

    @Override
    public Transaction reverse(String cardId, String transactionId, BigDecimal amount) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        Transaction transaction = card.getTransactions().stream().filter(t -> t.getId().equals(transactionId)).findFirst().orElse(null);
        if (transaction == null) {
            throw new TransactionNotFound();
        }
        if (transaction.isCaptured()) {
            throw new CardException("Transaction already captured!");
        }
        if (amount.compareTo(transaction.getAmount()) > 0) {
            throw new CardException("Cannot reverse more that was authorized!");
        }

        transaction.setAmount(transaction.getAmount().subtract(amount));
        card.setAvailableBalance(card.getAvailableBalance().add(amount));
        card.setBlockedBalance(card.getBlockedBalance().subtract(amount));

        cardRepository.save(card);

        return transaction;
    }

    @Override
    public Transaction refund(String cardId, String transactionId, BigDecimal amount) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        Transaction transaction = card.getTransactions().stream().filter(t -> t.getId().equals(transactionId)).findFirst().orElse(null);
        if (transaction == null) {
            throw new TransactionNotFound();
        }
        if (!transaction.isCaptured()) {
            throw new CardException("Transaction not captured yet!");
        }
        if (amount.compareTo(transaction.getAmount()) > 0) {
            throw new CardException("Cannot refund more that was authorized!");
        }

        transaction.setAmount(transaction.getAmount().subtract(amount));
        card.setAvailableBalance(card.getAvailableBalance().add(amount));
        card.setBlockedBalance(card.getBlockedBalance().subtract(amount));

        cardRepository.save(card);

        return transaction;
    }

    @Override
    public Card createCard(String cardId) {
        return cardRepository.save(new Card(cardId));
    }

    @Override
    public Card credit(String cardId, BigDecimal amount) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        card.setAvailableBalance(card.getAvailableBalance().add(amount));

        cardRepository.save(card);

        return card;
    }

    @Override
    public List<Transaction> getTransactionHistory(String cardId) throws CardException {
        Card card = cardRepository.findOne(cardId);
        if (card == null) {
            throw new CardNotFound();
        }

        return card.getTransactions();
    }
}
