package com.lich.cardsystem.entities;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leszek on 05/08/18.
 */
@Entity(name = "card")
public class Card {

    public Card(String id) {
        this.id = id;
    }

    public Card() {}

    @Id
    private String id;

    @Column(name="availableBalance")
    private BigDecimal availableBalance = new BigDecimal(0);

    @Column(name="blockedBalance")
    private BigDecimal blockedBalance = new BigDecimal(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "card")
    @Cascade(CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getBlockedBalance() {
        return blockedBalance;
    }

    public void setBlockedBalance(BigDecimal blockedBalance) {
        this.blockedBalance = blockedBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
