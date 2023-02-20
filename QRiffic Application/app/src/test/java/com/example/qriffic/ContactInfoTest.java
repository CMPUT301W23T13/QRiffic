package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// TEST COMMENT

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
        assertEquals("Mockistan",mockCI().getCountry());
        assertEquals("Mockopolis",mockCI().getCity());
        assertEquals("123.456.7890",mockCI().getPhone());
        assertEquals("mock@mockers.com",mockCI().getEmail());
    }

    @Test
    void testSetters(){

    }
}
