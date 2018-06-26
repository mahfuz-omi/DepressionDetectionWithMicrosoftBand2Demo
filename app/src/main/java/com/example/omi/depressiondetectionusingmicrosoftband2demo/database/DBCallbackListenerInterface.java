package com.example.omi.depressiondetectionusingmicrosoftband2demo.database;

/**
 * Created by USER on 11/27/2017.
 */

public interface DBCallbackListenerInterface {
    void beforeDBCall(int resourceIdentifier);
    void afterDBCall(int resourceIdentifier, Object response);
}
