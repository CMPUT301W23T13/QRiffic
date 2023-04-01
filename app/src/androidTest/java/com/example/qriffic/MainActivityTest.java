package com.example.qriffic;


import static junit.framework.TestCase.*;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.*;

/**
 * Test class for MainActivity. All the UI tests are written here.
 * Robotium test framework is used
 * Assumes that the app is freshly installed on the device and the robot's username is not in the database
 */
public class MainActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() throws Exception {
        MainActivity activity = rule.getActivity();
    }

    @Test
    public void checkActivity() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Test to see if the navigation menu takes us to the correct fragment
     */
    @Test
    public void checkFragment() throws InterruptedException { //Remove the throws InterruptedException if you don't want to use sleep()
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        FragmentManager fragmentManager = rule.getActivity().getFragmentManager();
        // check all fragments in fragment manager


//        // check if qr dex fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        sleep(5000);
//        solo.clickOnView(solo.getView(R.id.QRDex));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_qrdex);
//        assertTrue(fragment != null && fragment.isVisible());
//
//        // check if user profile fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        solo.clickOnView(solo.getView(R.id.action_profile));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_user_profile);
//        assertTrue(fragment != null && fragment.isVisible());
//
//        // check if leaderboard fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        solo.clickOnView(solo.getView(R.id.leaderboard));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_leaderboard);
//        assertTrue(fragment != null && fragment.isVisible());
//
//        // check if search users fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        solo.clickOnView(solo.getView(R.id.search_users));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_search_user);
//        assertTrue(fragment != null && fragment.isVisible());
//
//        // check if scan qr fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        solo.clickOnView(solo.getView(R.id.scan_QR));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_temp_add_qr);
//        assertTrue(fragment != null && fragment.isVisible());
//
//        // check if map fragment is visible
//        solo.clickOnActionBarItem(R.id.toolbar);
//        solo.clickOnView(solo.getView(R.id.map));
//        fragment = fragmentManager.findFragmentById(R.id.fragment_map);
//        assertTrue(fragment != null && fragment.isVisible());
    }



    /**
     * Test to see if the navigation menu takes us to the correct fragment
     */
    @Test
    public void checkNavigationMenu() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //open nav drawer
        solo.clickOnImageButton(0);

        Activity currentActivity = solo.getCurrentActivity();
        FragmentManager fragmentManager = currentActivity.getFragmentManager();
        Fragment fragment;

        //click on item in the nav drawer
        solo.clickOnView(solo.getView(R.id.nav_map));
        // Wait for MapFragment to become visible
        solo.waitForFragmentById(R.id.fragment_map, 5000);
        // Verify that MapFragment is visible
        FrameLayout mapLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_map);
        assertNotNull(mapLayout);
        assertTrue(mapLayout.isShown());



        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if search fragment is visible
        solo.clickOnView(solo.getView(R.id.nav_searchUser));
        //solo.clickOnText("Search Profiles");
        // Wait for SearchFragment to become visible
        solo.waitForFragmentById(R.id.fragment_search_user, 5000);
        // Verify that SearchFragment is visible
        FrameLayout searchLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_search_user);
        assertNotNull(searchLayout);
        assertTrue(searchLayout.isShown());


        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if leaderboard fragment is visible
        solo.clickOnView(solo.getView(R.id.nav_leaderboard));
        //solo.clickOnText("Leaderboard");
        // Wait for LeaderboardFragment to become visible
        solo.waitForFragmentById(R.id.fragment_leaderboard, 5000);
        // Verify that LeaderboardFragment is visible
        FrameLayout leaderboardLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_leaderboard);
        assertNotNull(leaderboardLayout);
        assertTrue(leaderboardLayout.isShown());






    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
