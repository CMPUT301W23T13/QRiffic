package com.example.qriffic;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qriffic.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String playerUsername;
    private DBAccessor dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // initialize firebase
        dba = new DBAccessor();
//        deletePlayerUsername(); // uncomment to delete uniqueID file and test 1st visit or not
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String fetchedUsername = fetchPlayerUsername();
        Bundle bundle = new Bundle();

        //TEMPORARY TEST CODE BLOCK (DELETE WHEN DONE)
        ArrayList<QRCode> testList = new ArrayList<QRCode>();
        testList.add(new QRCode("one", null, null));
        testList.add(new QRCode("two", null, null));
        testList.add(new QRCode("three", null, null));

        dba.setPlayer(
                new PlayerProfile("testName", "testEmail",
                    "testPhoneNum", 420, 69, testList)
        );
        PlayerProfile fetchedPlayer = new PlayerProfile(null, null,
            null, 0, 0, new ArrayList<>());
        //PlayerProfile player = dba.getPlayer("testName");
        dba.getPlayer(fetchedPlayer, "testName");
        //fetchedPlayer.setEmail("testEmail2");
//        dba.setPlayer(fetchedPlayer);
        //END TEMPORARY TEST CODE BLOCK


        if (fetchedUsername == null) {
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_QRDex_to_ProfileCreate, bundle);
            Toast.makeText(this, "1st visit", Toast.LENGTH_SHORT).show();
            String enteredUsername = "PLACEHOLDER USERNAME"; // TODO: get username from gui input and replace this placeholder with that.
            writeUsernameToFile(enteredUsername);
            bundle.putString("secretID", playerUsername);
        }else{
            this.playerUsername = fetchedUsername;
            bundle.putString("secretID", playerUsername);
            //this is a placeholder until someone can figure out how to pass a bundle to the
            // default screen--a splash screen solves this, but for now we have a hack
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_QRDex_to_ProfileCreate);
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_ProfileCreate_to_QRDex, bundle);
            Toast.makeText(this, "not 1st visit", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(binding.toolbar);
    }

    /**
     * Deletes a playerUsername file if it exists
     */
    protected void deletePlayerUsername(){
        if (fetchPlayerUsername() != null) {
            getApplicationContext().deleteFile("player-username");
        }
    }

    /**
     * Checks if a playerUsername has already been written to a file
     * @return
     *     playerUsername if a playerUsername has already been written to a file
     *     null otherwise
     */
    protected String fetchPlayerUsername() {
        try {
            // Try to read the uniqueID from file
            FileInputStream UsernameInputStream = getApplicationContext().openFileInput("player-username");
            byte[] playerUsernameBytes = new byte[36];
            UsernameInputStream.read(playerUsernameBytes);
            playerUsername = "";
            int i = 0;
            while (playerUsernameBytes[i] != 0) {
                playerUsername += (char) playerUsernameBytes[i];
                i++;
            }
        } catch (Exception FileNotFoundException) {
            // No uniqueID file found
            return null;
        }
        return playerUsername;
    }

    /**
     * Writes the given username to a file
     * @param username
     *     username to be written to file
     */
    protected void writeUsernameToFile(String username) {
        File playerUsernameFile = new File(getApplicationContext().getFilesDir(), "player-username");
        try {
            playerUsernameFile.createNewFile();
            FileOutputStream playerUsernameOutputStream = new FileOutputStream(playerUsernameFile);
            playerUsernameOutputStream.write(username.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.search_users:
                changeFragment(new FragmentSearchUser());
            break;
            case R.id.leaderboard:
                changeFragment(new FragmentLeaderboard());
            break;
            case R.id.action_profile:
                changeFragment(new FragmentUserProfile());
                break;
            case R.id.qr_dex:
                changeFragment(new FragmentQRDex());
                break;
            case R.id.map:
                changeFragment(new FragmentMap());
                break;
            case R.id.scan_QR:
                changeFragment(new FragmentTempAddQr()); // TEMP FRAGMENT!
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_cont);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void changeFragment(Fragment fr){
        FrameLayout fl = (FrameLayout) findViewById(R.id.nav_host_fragment_content_main);
        fl.removeAllViews();
        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.nav_host_fragment_content_main, fr);
        transaction1.commit();
    }
}