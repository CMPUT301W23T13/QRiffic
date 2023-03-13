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

        // check if user profile fragment is visible
        Fragment fragment = fragmentManager.findFragmentById(R.id.user_profile);
        assertTrue(fragment != null && fragment.isVisible());

        //solo.clickOnView(R.id.whatever the three dots view is called)
        //click on one of the menu options
        // check if fragment is visible

        //... do this for every frag




    }




    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
