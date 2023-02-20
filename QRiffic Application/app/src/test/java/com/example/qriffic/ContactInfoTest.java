package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ContactInfo class
 */
public class ContactInfoTest {

    private ContactInfo mockCI(){
        return new ContactInfo(
                "Mockistan",
                "Mockopolis",
                "123.456.7890",
                "mock@mockers.com");
    }

    @Test
    void testGetters(){
        assertEquals("Mockistan",mockCI().getCity());
    }

}
