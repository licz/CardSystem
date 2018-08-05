package com.lich.cardsystem.repositories;

import com.lich.cardsystem.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by leszek on 05/08/18.
 */
public interface TransactionRepository extends CrudRepository<Transaction, String> {
}
