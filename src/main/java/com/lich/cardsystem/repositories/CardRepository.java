package com.lich.cardsystem.repositories;

import com.lich.cardsystem.entities.Card;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by leszek on 05/08/18.
 */
@Repository
public interface CardRepository extends CrudRepository<Card, String> {
}
