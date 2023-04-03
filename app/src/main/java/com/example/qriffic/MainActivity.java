package com.example.qriffic;

import android.os.Bundle;
import android.view.Menu;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.qriffic.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private final boolean mLocationPermissionGranted = false;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private UsernamePersistent usernamePersistent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        usernamePersistent = new UsernamePersistent(getApplicationContext());

        // new drawer layout
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        // these JUST tell the navigator that these destinations are top level. You can add
        // to the navigation menu and it will still navigate

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_scanner,
                R.id.nav_map,
                R.id.nav_userProfile,
                R.id.nav_searchUser,
                R.id.nav_leaderboard,
                R.id.nav_ProfileUpdate,
                R.id.nav_devAddQR)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    /**
     * Hide the toolbar
     */
    public void hideToolbar() {
        getSupportActionBar().hide();
    }

    /**
     * Show the toolbar
     */
    public void showToolbar() {
        getSupportActionBar().show();
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void changeFragment(Fragment fr) {
        FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentContainerView);
        fl.removeAllViews();

        String username = usernamePersistent.fetchUsername();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        fr.setArguments(bundle);

        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction().setReorderingAllowed(true);
        transaction1.add(R.id.fragmentContainerView, fr);

        transaction1.commit();
    }
}