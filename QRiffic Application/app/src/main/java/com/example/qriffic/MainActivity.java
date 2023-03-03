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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //deleteUniqueID();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        if (uniqueIDExists()) { //not 1st visit
            setContentView(binding.getRoot());
            Toast.makeText(this, "not 1st visit", Toast.LENGTH_SHORT).show();
        }else{ //1st visit
            this.uniqueID = generateUniqueID();
            setContentView(binding.getRoot());
            Toast.makeText(this, "1st visit", Toast.LENGTH_SHORT).show();
        }
        setSupportActionBar(binding.toolbar);
    }

    /**
     * Deletes a uniqueID file if it exists
     */
    private void deleteUniqueID(){
        if (uniqueIDExists() == true) {
            getApplicationContext().deleteFile("unique-id");
        }
    }


    /**
     * Checks if a UniqueID has already been generated
     * @return
     *     true if a UniqueID has already been generated (uniqueID file exists)
     *     false otherwise
     */
    private Boolean uniqueIDExists() {
        try {
            // Try to read the uniqueID from file
            FileInputStream secretIDInputStream = getApplicationContext().openFileInput("unique-id");
            byte[] uniqueIDBytes = new byte[36];
            secretIDInputStream.read(uniqueIDBytes);
            uniqueID = "";
            for (int i = 0; i < 36; i++) {
                uniqueID += (char) uniqueIDBytes[i];
            }
        } catch (Exception FileNotFoundException) {
            // No uniqueID file found, generate uniqueID and save to file
            return false;
        }
        return true;
    }

    /**
     * Generates UniqueID and saves it to a file
     * @return
     *      uniqueID: a string representation of a UUID
     */
    private String generateUniqueID() {
        String uniqueID;
        // Generate uniqueID and save to file
        uniqueID = UUID.randomUUID().toString();
        File secretIDFile = new File(getApplicationContext().getFilesDir(), "unique-id");
        try {
            secretIDFile.createNewFile();
            FileOutputStream secretIDOutputStream = new FileOutputStream(secretIDFile);
            secretIDOutputStream.write(uniqueID.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uniqueID;
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
                changeFragment(new SearchUser());
            break;
            case R.id.leaderboard:
                changeFragment(new Leaderboard());
            break;
            case R.id.action_profile:
                changeFragment(new UserProfile());
                break;
            case R.id.qr_dex:
                changeFragment(new QRDex());
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