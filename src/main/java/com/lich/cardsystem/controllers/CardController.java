package com.lich.cardsystem.controllers;

import com.lich.cardsystem.entities.Card;
import com.lich.cardsystem.entities.Transaction;
import com.lich.cardsystem.exceptions.CardException;
import com.lich.cardsystem.processor.CardProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by leszek on 05/08/18.
 */
@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    public CardController(CardProcessor cardProcessor) {
        this.cardProcessor = cardProcessor;
    }

    private CardProcessor cardProcessor;

    @RequestMapping(value="/card/{cardId}", method = RequestMethod.POST)
    public @ResponseBody
    Card createCard(@PathVariable("cardId") String cardId) {
        return cardProcessor.createCard(cardId);
    }

    @RequestMapping(value="/card/{cardId}/credit/{amount}", method = RequestMethod.POST)
    public @ResponseBody
    Card credit(@PathVariable("cardId") String cardId, @PathVariable("amount") String amount) throws CardException {
        return cardProcessor.credit(cardId, new BigDecimal(amount));
    }

    @RequestMapping(value="/card/{cardId}/authorize/{amount}", method = RequestMethod.GET)
    public @ResponseBody
    String authorizePayment(@PathVariable("cardId") String cardId, @PathVariable("amount") String amount) throws CardException {
        return cardProcessor.authorize(cardId, new BigDecimal(amount));
    }

    @RequestMapping(value="/card/{cardId}/capture/{transactionId}", method = RequestMethod.GET)
    public @ResponseBody
    Transaction capture(@PathVariable("cardId") String cardId, @PathVariable("transactionId") String transactionId) throws CardException {
        return cardProcessor.capture(cardId, transactionId);
    }

    @RequestMapping(value="/card/{cardId}/reverse/{transactionId}/{amount}", method = RequestMethod.GET)
    public @ResponseBody
    Transaction reverse(@PathVariable("cardId") String cardId, @PathVariable("transactionId") String transactionId, @PathVariable("amount") String amount) throws CardException {
        return cardProcessor.reverse(cardId, transactionId, new BigDecimal(amount));
    }

    @RequestMapping(value="/card/{cardId}/refund/{transactionId}/{amount}", method = RequestMethod.GET)
    public @ResponseBody
    Transaction refund(@PathVariable("cardId") String cardId, @PathVariable("transactionId") String transactionId, @PathVariable("amount") String amount) throws CardException {
        return cardProcessor.refund(cardId, transactionId, new BigDecimal(amount));
    }

    @RequestMapping(value="/card/{cardId}/transactionHistory", method = RequestMethod.GET)
    public @ResponseBody
    List<Transaction> getTransactionHistory(@PathVariable("cardId") String cardId) throws CardException {
        return cardProcessor.getTransactionHistory(cardId);
    }
}
