package com.example.qriffic;


import static junit.framework.TestCase.*;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

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
    public void checkFragment() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        Activity currentActivity = solo.getCurrentActivity();
        FragmentManager fragmentManager = currentActivity.getFragmentManager();
        Fragment fragment;

        // check if qr dex fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(1);
        fragment = fragmentManager.findFragmentById(R.id.fragment_qrdex);
        assertTrue(fragment != null && fragment.isVisible());

        // check if user profile fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(2);
        fragment = fragmentManager.findFragmentById(R.id.fragment_user_profile);
        assertTrue(fragment != null && fragment.isVisible());

        // check if leaderboard fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(3);
        fragment = fragmentManager.findFragmentById(R.id.fragment_leaderboard);
        assertTrue(fragment != null && fragment.isVisible());

        // check if search users fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(4);
        fragment = fragmentManager.findFragmentById(R.id.fragment_search_user);
        assertTrue(fragment != null && fragment.isVisible());

        // check if scan qr fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(5);
        fragment = fragmentManager.findFragmentById(R.id.fragment_temp_add_qr);
        assertTrue(fragment != null && fragment.isVisible());

        // check if map fragment is visible
        solo.clickOnActionBarItem(R.id.toolbar);
        solo.clickInList(6);
        fragment = fragmentManager.findFragmentById(R.id.fragment_map);
        assertTrue(fragment != null && fragment.isVisible());
    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
