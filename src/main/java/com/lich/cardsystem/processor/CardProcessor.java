package com.lich.cardsystem.processor;

import com.lich.cardsystem.entities.Card;
import com.lich.cardsystem.entities.Transaction;
import com.lich.cardsystem.exceptions.CardException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by leszek on 05/08/18.
 */
public interface CardProcessor {
    String authorize(String cardId, BigDecimal amount) throws CardException;
    Transaction capture(String cardId, String transactionId) throws CardException;
    Transaction reverse(String cardId, String transactionId, BigDecimal amount) throws CardException;
    Transaction refund(String cardId, String transactionId, BigDecimal amount) throws CardException;
    Card createCard(String cardId);
    Card credit(String cardId, BigDecimal amount) throws CardException;
    List<Transaction> getTransactionHistory(String cardId) throws CardException;
    Card getCard(String cardId) throws CardException;
}
