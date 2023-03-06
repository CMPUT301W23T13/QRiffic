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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private String uniqueID;
    private DBAccessor dba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // initialize firebase
        dba = new DBAccessor();
        //deleteUniqueID(); // uncomment to delete uniqueID file and test 1st visit or not
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String uid = fetchUniqueID();
        Bundle bundle = new Bundle();


        //TEMPORARY TEST CODE BLOCK (DELETE WHEN DONE)
        dba.setPlayer(
                new PlayerProfile("testName", "testUniqueID",
                        new ContactInfo("testCountry", "testCity", "testPhone", "testEmail")
                , 0, 0, new ArrayList<>())
        );
        //END TEMPORARY TEST CODE BLOCK

        if (uid == null) {
            this.uniqueID = generateUniqueID();
            bundle.putString("secretID", uniqueID);
            Navigation.findNavController(this, R.id.nav_host_fragment_content_main).navigate(R.id.action_QRDex_to_ProfileCreate, bundle);
            Toast.makeText(this, "1st visit", Toast.LENGTH_SHORT).show();
        }else{
            this.uniqueID = uid;
            bundle.putString("secretID", uniqueID);
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
     * Deletes a uniqueID file if it exists
     */
    protected void deleteUniqueID(){
        if (fetchUniqueID() != null) {
            getApplicationContext().deleteFile("unique-id");
        }
    }

    /**
     * Checks if a UniqueID has already been generated
     * @return
     *     true if a UniqueID has already been generated (uniqueID file exists)
     *     false otherwise
     */
    protected String fetchUniqueID() {
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
            // No uniqueID file found
            return null;
        }
        return uniqueID;
    }

    /**
     * Generates UniqueID and saves it to a file
     * @return
     *      uniqueID: a string representation of a UUID
     */
    protected String generateUniqueID() {
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