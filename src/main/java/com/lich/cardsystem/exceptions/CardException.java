package com.lich.cardsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by leszek on 05/08/18.
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class CardException extends Exception {

    public CardException() {}

    public CardException(String s) {
        super(s);
    }
}
