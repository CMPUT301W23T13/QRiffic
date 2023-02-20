package com.example.qriffic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for ContactInfo class
 */
public class ContactInfoTest {

    private ContactInfo mockCI() {
        return new ContactInfo(
                "Mockistan",
                "Mockopolis",
                "123.456.7890",
                "mock@mockers.com");
    }

    @Test
    void testGetters() {

        ContactInfo mockCI = mockCI();

        assertEquals("Mockistan", mockCI.getCountry());
        assertNotEquals("Canada", mockCI.getCountry());

        assertEquals("Mockopolis", mockCI.getCity());
        assertNotEquals("Edmonton", mockCI.getCity());

        assertEquals("123.456.7890", mockCI.getPhone());
        assertNotEquals("111.111.1111", mockCI.getPhone());

        assertEquals("mock@mockers.com", mockCI.getEmail());
        assertNotEquals("bob@bob.com", mockCI.getEmail());
    }
}
