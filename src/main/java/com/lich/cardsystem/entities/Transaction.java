package com.lich.cardsystem.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;

/**
 * Created by leszek on 05/08/18.
 */
@Entity(name = "transaction")
public class Transaction {

    public Transaction(String id, Card card, BigDecimal amount) {
        this.id = id;
        this.card = card;
        this.amount = amount;
    }

    public Transaction () {}

    @Id
    private String id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Card card;
    @Column
    private BigDecimal amount;
    @Column
    private boolean captured;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
    }
}
