package org.warmsheep.encoder.security.exception;

/**
 * Created by ft on 2017/4/25.
 */
public class CryptoException extends Exception {

    public CryptoException() {
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}