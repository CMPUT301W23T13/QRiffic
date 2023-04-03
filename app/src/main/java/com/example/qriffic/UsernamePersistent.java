package com.example.qriffic;

import android.content.Context;
import android.icu.lang.UScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Keeps track of the user's username, which is persistent and saved on the disk
 */
public class UsernamePersistent {
    private Context context;

    public UsernamePersistent(Context context) {
        this.context = context;
    }

    /**
     * Checks if a username has been saved
     *
     * @return true if a username has already been saved (username file exists)
     * false otherwise
     */
    public String fetchUsername() {
        String username;
        try {
            // Try to read the username from file
            //FileInputStream secretIDInputStream = requireActivity().getApplicationContext().openFileInput("username");
            FileInputStream secretIDInputStream = context.openFileInput("username");
            byte[] uniqueIDBytes = new byte[16];
            secretIDInputStream.read(uniqueIDBytes);
            username = "";
            for (int i = 0; i < 16; i++) {
                if (uniqueIDBytes[i] == 0) {
                    break;
                }
                username += (char) uniqueIDBytes[i];
            }
        } catch (Exception FileNotFoundException) {
            // No username file found
            return null;
        }
        return username;
    }

    /**
     * Saves username to file for persistence
     * @param username
     */
    protected void saveUsername(String username) {
        File secretIDFile = new File(context.getFilesDir(), "username");
        try {
            secretIDFile.createNewFile();
            FileOutputStream secretIDOutputStream = new FileOutputStream(secretIDFile);
            secretIDOutputStream.write(username.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a username file if it exists
     */
    protected void deleteUsername() {
        if (fetchUsername() != null) {
            context.deleteFile("username");
        }
    }

}
