package com.example.qriffic;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
                R.id.userProfile, R.id.leaderboard2)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Fragment fragment = null;
//        Class fragmentClass = null;
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        switch(id){
//            case R.id.search_users:
//                changeFragment(new FragmentSearchUser());
//            break;
//            case R.id.leaderboard:
//                changeFragment(new FragmentLeaderboard());
//            break;
//            case R.id.action_profile:
//                changeFragment(new FragmentUserProfile());
//                break;
//            case R.id.map:
//                changeFragment(new FragmentMap());
//                break;
//            case R.id.scan_QR:
//                changeFragment(new FragmentScanner());
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void changeFragment(Fragment fr){
        FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentContainerView);
        fl.removeAllViews();

        String username = usernamePersistent.fetchUsername();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        fr.setArguments(bundle);

        FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
        transaction1.add(R.id.fragmentContainerView, fr);

        transaction1.commit();
    }
}