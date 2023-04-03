package com.example.qriffic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class QRDataTest {
    private QRCode mockQRCode() {
        return new QRCode("abcdef", new GeoLocation(11.11, 22.22, "Edmonton"), "username", null, "a comment");
    }

    private PlayerProfile mockPlayerProfile() {
        QRCode mockQR = mockQRCode();
        return new PlayerProfile("username2", "username2@outlook.com", "999.999.9999",  new HashMap<String, QRCode>(){{put(mockQR.getIdHash(), mockQR);}});
    }

    private QRData mockQRData() {
        QRCode mockQR = mockQRCode();
        return new QRData(mockQR);
    }

    @Test
    void testGetters() {
        ObjectMapper mapper = new ObjectMapper();
        QRCode mockQRCode = mockQRCode();
        QRData mockQRData = mockQRData();
        PlayerProfile mockPlayer = mockPlayerProfile();

        assertEquals(mockQRData.getIdHash(), mockQRCode.getIdHash());
        assertNotEquals(mockQRData.getIdHash(), "123");

        assertEquals(mockQRData.getScore(), mockQRCode.getScore());
        assertNotEquals(mockQRData.getScore(), 123);

        assertEquals(mockQRData.getName(), mockQRCode.getName());
        assertNotEquals(mockQRData.getName(), "123");

        assertEquals(mockQRData.getUsers().get(mockQRCode.getUsername()).get("comment"), mockQRCode.getComment());
        assertNotEquals(mockQRData.getUsers().get(mockQRCode.getUsername()).get("comment"), "123");

        assertEquals(mockQRData.getUsers().get(mockQRCode.getUsername()).get("username"), mockQRCode.getUsername());
        assertNotEquals(mockQRData.getUsers().get(mockQRCode.getUsername()).get("username"), "123");

        GeoLocation QRDataLoc = mapper.convertValue(mockQRData.getUsers().get(mockQRCode.getUsername()).get("geoLocation"), GeoLocation.class);
        assertEquals(QRDataLoc.getLatitude(), mockQRCode.getGeoLocation().getLatitude());
        assertEquals(QRDataLoc.getLongitude(), mockQRCode.getGeoLocation().getLongitude());
        assertEquals(QRDataLoc.getCity(), mockQRCode.getGeoLocation().getCity());
        assertNotEquals(QRDataLoc.getLatitude(), 123.123);
        assertNotEquals(QRDataLoc.getLongitude(), 123.123);
        assertNotEquals(QRDataLoc.getCity(), "123");
    }

    @Test
    void testSetters() {
        QRData mockQRData = mockQRData();
        PlayerProfile mockPlayer = mockPlayerProfile();

        mockQRData.setScore(123);
        assertEquals(mockQRData.getScore(), 123);
        assertNotEquals(mockQRData.getScore(), 0);

        mockQRData.setName("123");
        assertEquals(mockQRData.getName(), "123");
        assertNotEquals(mockQRData.getName(), "abc");

        mockQRData.setIdHash("123");
        assertEquals(mockQRData.getIdHash(), "123");
        assertNotEquals(mockQRData.getIdHash(), "abc");

        HashMap<String, HashMap<String, Object>> users = new HashMap<String, HashMap<String, Object>>() {{
            put("username123", new HashMap<String, Object>() {{
                put("comment", "Unique Comment 123");
                put("username", "username123");
                put("geoLocation", new GeoLocation(11.11, 22.22, "Edmonton"));
            }});
        }};
        mockQRData.setUsers(users);
        assertEquals(mockQRData.getUsers().get("username123").get("comment"), "Unique Comment 123");
        assertNotEquals(mockQRData.getUsers(), new HashMap<>());

        QRCode tempQRCode = mockQRCode();
        tempQRCode.setUsername("username1234");
        int before = mockQRData.getUsers().size();
        mockQRData.addUser(tempQRCode);
        assertEquals(mockQRData.getUsers().size(), before + 1);

        before = mockQRData.getUsers().size();
        mockQRData.removeUser(tempQRCode.getUsername());
        assertEquals(mockQRData.getUsers().size(), before - 1);
    }
}
