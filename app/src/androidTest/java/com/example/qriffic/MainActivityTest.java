package com.example.qriffic;


import static junit.framework.TestCase.*;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
    public void checkNavigationMenu() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //open nav drawer
        solo.clickOnImageButton(0);

        Activity currentActivity = solo.getCurrentActivity();
        FragmentManager fragmentManager = currentActivity.getFragmentManager();
        Fragment fragment;

        //click on item in the nav drawer
        solo.clickOnText("QRs Nearby");
        // Wait for MapFragment to become visible
        solo.waitForFragmentById(R.id.fragment_map, 5000);
        // Verify that MapFragment is visible
        FrameLayout mapLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_map);
        assertNotNull(mapLayout);
        assertTrue(mapLayout.isShown());



        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if search fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_searchUser));
        solo.clickOnText("Search Profiles");
        // Wait for SearchFragment to become visible
        solo.waitForFragmentById(R.id.fragment_search_user, 5000);
        // Verify that SearchFragment is visible
        FrameLayout searchLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_search_user);
        assertNotNull(searchLayout);
        assertTrue(searchLayout.isShown());


        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if leaderboard fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_leaderboard));
        solo.clickOnText("Leaderboard");
        // Wait for LeaderboardFragment to become visible
        solo.waitForFragmentById(R.id.fragment_leaderboard, 5000);
        // Verify that LeaderboardFragment is visible
        FrameLayout leaderboardLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_leaderboard);
        assertNotNull(leaderboardLayout);
        assertTrue(leaderboardLayout.isShown());


        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if profile fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_profile));
        solo.clickOnText("My Profile");
        // Wait for ProfileFragment to become visible
        solo.waitForFragmentById(R.id.fragment_user_profile, 5000);
        // Verify that ProfileFragment is visible
        FrameLayout profileLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_user_profile);
        assertNotNull(profileLayout);
        assertTrue(profileLayout.isShown());

        //click on nav drawer button
        solo.clickOnImageButton(0);

        //check if scan fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_scan));
        solo.clickOnText("Snap a QR");
        // Wait for ScanFragment to become visible
        solo.waitForFragmentById(R.id.capture_layout, 5000);
        // Verify that ScanFragment is visible
        FrameLayout scanLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.capture_layout);
        assertNotNull(scanLayout);
        assertTrue(scanLayout.isShown());

        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if update fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_update));
        solo.clickOnText("Update Profile");
        // Wait for UpdateFragment to become visible
        solo.waitForFragmentById(R.id.profile_crt, 5000);
        // Verify that UpdateFragment is visible
        FrameLayout updateLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.profile_crt);
        assertNotNull(updateLayout);
        assertTrue(updateLayout.isShown());



    }

    /**
     * Test to search for a user
     */
    @Test
    public void checkSearchUser() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //open nav drawer
        solo.clickOnImageButton(0);
        //click on search user
        solo.clickOnText("Search Profiles");
        // Wait for SearchFragment to become visible
        solo.waitForFragmentById(R.id.fragment_search_user, 5000);
        // Verify that SearchFragment is visible
        FrameLayout searchLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_search_user);
        assertNotNull(searchLayout);
        assertTrue(searchLayout.isShown());

        //search for a user
        solo.enterText(0, "Luke007");
        solo.clickOnView(solo.getView(R.id.search_button));
        //wait for the search to complete
        sleep(5000);
        //check if the user is displayed
        solo.waitForFragmentById(R.id.fragment_user_profile, 5000);
        FrameLayout profileLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_user_profile);
        assertNotNull(profileLayout);
        assertTrue(profileLayout.isShown());

        //check if the user is displayed
        //check if the textview has the correct text
        TextView username = (TextView) solo.getView(R.id.user_name);
        assertEquals("Luke007", username.getText().toString());


    }

    /**
     * Test to check if you can see QR details
     */
    @Test
    public void checkQRDetails() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        TextView qrName = (TextView) solo.getView(R.id.pListName);

        //click on list view item
        solo.clickInList(0);
        //wait for the details to load
        solo.waitForFragmentById(R.id.fragment_qr_detail, 5000);
        //check if the details are displayed
        RelativeLayout detailLayout = (RelativeLayout) solo.getCurrentActivity().findViewById(R.id.fragment_qr_detail);
        assertNotNull(detailLayout);
        assertTrue(detailLayout.isShown());

        //check if the details are displayed
        //check if the textview has the correct text

        TextView qrNameDetail = (TextView) solo.getView(R.id.qr_detail_name);


        //check if the textview has the correct text
        assertEquals(qrName.getText().toString(), qrNameDetail.getText().toString());

    }


    /**
     * Test to check if you can delete a QR
     */
    @Test
    public void checkDeleteQR() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        TextView qrName = (TextView) solo.getView(R.id.pListName);

        ListView listView = (ListView) solo.getView(R.id.profileList);
        //get adapter
        QRCodeAdapter adapter = (QRCodeAdapter) listView.getAdapter();

        //get the number of items in the list
        int count = adapter.getCount();



        //click on list view item
        solo.clickInList(0);
        //wait for the details to load
        solo.waitForFragmentById(R.id.fragment_qr_detail, 5000);
        //check if the details are displayed
        RelativeLayout detailLayout = (RelativeLayout) solo.getCurrentActivity().findViewById(R.id.fragment_qr_detail);
        assertNotNull(detailLayout);
        assertTrue(detailLayout.isShown());

        //click on delete button
        solo.clickOnView(solo.getView(R.id.fab_delete_qr));

        solo.waitForFragmentById(R.id.fragment_user_profile, 5000);

        //check if the list has been updated
        ListView listView2 = (ListView) solo.getView(R.id.profileList);
        //get adapter
        QRCodeAdapter adapter2 = (QRCodeAdapter) listView.getAdapter();

        //get the number of items in the list
        int count2 = adapter2.getCount();

        //check if the list has been updated
        assertEquals(count-1, count2-1);

    }


    //check if leaderboards are displayed
    @Test
public void checkLeaderboards() throws Exception {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //click on nav drawer button
        solo.clickOnImageButton(0);
        //check if leaderboard fragment is visible
//        solo.clickOnView(solo.getView(R.id.nav_leaderboard));
        solo.clickOnText("Leaderboard");
        // Wait for LeaderboardFragment to become visible
        solo.waitForFragmentById(R.id.fragment_leaderboard, 5000);
        // Verify that LeaderboardFragment is visible
        FrameLayout leaderboardLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_leaderboard);
        assertNotNull(leaderboardLayout);
        assertTrue(leaderboardLayout.isShown());

        solo.clickOnView(solo.getView(R.id.leaderboard_spinner));
        solo.clickOnText("Top Player Scores");
        sleep(2000);
        TextView title = (TextView) solo.getView(R.id.leaderboard_type);
        assertEquals("Top Points Globally", title.getText().toString());

        solo.clickOnView(solo.getView(R.id.leaderboard_spinner));
        solo.clickOnText("Most QRMons Scanned");
        sleep(2000);
        TextView title2 = (TextView) solo.getView(R.id.leaderboard_type);
        assertEquals("Most Scans Globally", title2.getText().toString());

        solo.clickOnView(solo.getView(R.id.leaderboard_spinner));
        solo.clickOnText("Top QRMons Globally");
        sleep(2000);
        TextView title3 = (TextView) solo.getView(R.id.leaderboard_type);
        assertEquals("Top QRMons Globally", title3.getText().toString());


    }

    /**
     * Test to check if you can click on markers and see qr code details
     */

    @Test
    public void checkMarkerClick() throws Exception{
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        //click on nav drawer button
        solo.clickOnImageButton(0);
        //click on map
        solo.clickOnText("QRs Nearby");
        //wait for map to load
        solo.waitForFragmentById(R.id.fragment_map, 5000);
        //check if map fragment is visible
        FrameLayout mapLayout = (FrameLayout) solo.getCurrentActivity().findViewById(R.id.fragment_map);
        assertNotNull(mapLayout);
        assertTrue(mapLayout.isShown());

        // Get a reference to the GoogleMap object
        MapFragment mapFragment = (MapFragment) solo.getCurrentActivity().getFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        // Get a reference to the GoogleMap object
                        GoogleMap map = googleMap;

                        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                // Get the marker position
                                Marker markerOptions = map.addMarker(new MarkerOptions().position(marker.getPosition()));
                                LatLng markerPosition = markerOptions.getPosition();

                                // Click on the marker position
                                solo.clickOnScreen((int) markerPosition.longitude, (int) markerPosition.latitude);




                                return false;
                            }
                        });


                    }
                }
        );



    }


    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
