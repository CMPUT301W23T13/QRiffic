package com.example.qriffic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewGroupCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentLeaderboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLeaderboard extends Fragment implements LocationListener {
    private long mLastClickTime = 0;
    private ArrayList<LeaderboardEntry> dataList;
    private LeaderboardAdapter leaderboardAdapter;

    public FragmentLeaderboard() {
        // Required empty public constructor
    }

    public static FragmentLeaderboard newInstance() {
        FragmentLeaderboard fragment = new FragmentLeaderboard();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Adds transitions
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        postponeEnterTransition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        TextView leaderboardPlayerTitle = view.findViewById(R.id.leaderboard_player_title);
        TextView myName = view.findViewById(R.id.my_name);
        TextView myPoints = view.findViewById(R.id.my_points);
        TextView myRankNumber = view.findViewById(R.id.my_rank_number);
        TextView leaderboardType = view.findViewById(R.id.leaderboard_type);
        ListView leaderboardList = view.findViewById(R.id.leaderboard_list);
        TextView noRegionText = view.findViewById(R.id.no_region_text);

        // Setting the dropdown menu
        Spinner spinner = (Spinner) view.findViewById(R.id.leaderboard_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.leaderboard_spinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ViewGroupCompat.setTransitionGroup(leaderboardList, true);
        ViewGroupCompat.setTransitionGroup(spinner, true);

        LeaderboardData data = new LeaderboardData();
        PlayerProfile profile = new PlayerProfile();
        String city = getCurrCity();

        String username = new UsernamePersistent(getContext()).fetchUsername();

        DBA.getPlayer(profile, username);

        // Waits for player info to be fetched before getting leaderboard data
        profile.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                DBA.getLeaderboard(data, city);
            }

            @Override
            public void onFetchFailure() {

            }
        });

        data.addListener(new fetchListener() {
            @Override
            public void onFetchComplete() {
                dataList = data.getTopPlayerPoints();

                leaderboardPlayerTitle.setText("My Score");

                myName.setText(profile.getUsername());
                myPoints.setText(Integer.toString(profile.getTotalScore()));
                spinner.setSelection(0);
                int myRank = 0;
                myName.setText(profile.getUsername());

                // Checks which rank you are at
                while (myRank < dataList.size()) {
                    if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                        break;
                    }
                    myRank += 1;
                }
                if (myRank < dataList.size()) {
                    myRankNumber.setText(Integer.toString(myRank + 1));
                } else {
                    myRankNumber.setText("N/A");
                }

                leaderboardType.setText("Top Points Globally");

                ArrayList<LeaderboardEntry> adapterList = new ArrayList<>();
                leaderboardAdapter = new LeaderboardAdapter(getContext(), adapterList);
                leaderboardAdapter.addAll(data.getTopPlayerPoints());
                leaderboardList.setAdapter(leaderboardAdapter);
                leaderboardAdapter.notifyDataSetChanged();
                startPostponedEnterTransition();
            }

            @Override
            public void onFetchFailure() {
            }
        });

        // Allows players to be clicked on to visit their profile
        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bundle bundle = new Bundle();
                LeaderboardEntry entry = leaderboardAdapter.getItem(position);
                bundle.putString("username", entry.getId());
                bundle.putBoolean("isUser", false);
                Navigation.findNavController(view).navigate(R.id.action_nav_leaderboard_to_fragmentUserSearchedProfile, bundle);
            }
        });

        // Listener for the dropdown menu, changes the leaderboard type
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = (String) parent.getItemAtPosition(position);
                if (Objects.nonNull(dataList)) {
                    if (Objects.equals(choice, "Top Player Scores")) {
                        // Changes leaderboard list listener type
                        leaderboardList.setOnItemClickListener(null);
                        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Bundle bundle = new Bundle();
                                LeaderboardEntry entry = leaderboardAdapter.getItem(position);
                                bundle.putString("username", entry.getId());
                                bundle.putBoolean("isUser", false);
                                Navigation.findNavController(view).navigate(R.id.action_nav_leaderboard_to_fragmentUserSearchedProfile, bundle);
                            }
                        });

                        // Fills leaderboard data with correct type
                        dataList = data.getTopPlayerPoints();
                        leaderboardPlayerTitle.setText("My Score");

                        myName.setText(profile.getUsername());
                        myPoints.setText(Integer.toString(profile.getTotalScore()));

                        // Checks rank of user
                        int myRank = 0;
                        while (myRank < dataList.size()) {
                            if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                                break;
                            }
                            myRank += 1;
                        }
                        if (myRank < dataList.size()) {
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        } else {
                            myRankNumber.setText("N/A");
                        }

                        leaderboardType.setText("Top Points Globally");

                        noRegionText.setVisibility(View.GONE);
                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopPlayerPoints());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Most QRMons Scanned")) {
                        // Changes leaderboard list listener type
                        leaderboardList.setOnItemClickListener(null);
                        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Bundle bundle = new Bundle();
                                LeaderboardEntry entry = leaderboardAdapter.getItem(position);
                                bundle.putString("username", entry.getId());
                                bundle.putBoolean("isUser", false);
                                Navigation.findNavController(view).navigate(R.id.action_nav_leaderboard_to_fragmentUserSearchedProfile, bundle);
                            }
                        });

                        // Fills leaderboard data with correct type
                        dataList = data.getTopPlayerScans();
                        leaderboardPlayerTitle.setText("My Scans");
                        myName.setText(profile.getUsername());
                        myPoints.setText(Integer.toString(profile.getTotalScanned()));

                        // Checks rank of user
                        int myRank = 0;
                        while (myRank < dataList.size()) {
                            if (Objects.equals(dataList.get(myRank).getId(), profile.getUsername())) {
                                break;
                            }
                            myRank += 1;
                        }
                        if (myRank < dataList.size()) {
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        } else {
                            myRankNumber.setText("N/A");
                        }

                        leaderboardType.setText("Most Scans Globally");

                        noRegionText.setVisibility(View.GONE);
                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopPlayerScans());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Top QRMons Globally")) {
                        // Changes leaderboard list listener type
                        leaderboardList.setOnItemClickListener(null);
                        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Bundle bundle = new Bundle();
                                LeaderboardEntry entry = leaderboardAdapter.getItem(position);
                                bundle.putString("QRID", entry.getId());
                                bundle.putBoolean("isUser", false);
                                Navigation.findNavController(view).navigate(R.id.action_nav_leaderboard_to_nav_QRDetail, bundle);
                            }
                        });

                        // Fills leaderboard data with correct type
                        dataList = data.getTopQRPoints();
                        leaderboardPlayerTitle.setText("My Best QRMon");

                        QRCode qr = profile.getBestQR();
                        if (qr == null) {
                            myName.setText("N/A");
                            myPoints.setText("N/A");
                            myRankNumber.setText("N/A");
                        } else {
                            // Checks rank of best QR
                            myName.setText(qr.getName());
                            myPoints.setText(Integer.toString(qr.getScore()));
                            int myRank = 0;
                            while (myRank < dataList.size()) {
                                if (Objects.equals(dataList.get(myRank).getId(), qr.getIdHash())) {
                                    break;
                                }
                                myRank += 1;
                            }
                            myRankNumber.setText(Integer.toString(myRank + 1));
                        }
                        leaderboardType.setText("Top QRMons Globally");

                        noRegionText.setVisibility(View.GONE);
                        leaderboardAdapter.clear();
                        leaderboardAdapter.addAll(data.getTopQRPoints());
                        leaderboardAdapter.notifyDataSetChanged();
                    } else if (Objects.equals(choice, "Top QRMons Regionally")) {
                        // Changes leaderboard list listener type
                        leaderboardList.setOnItemClickListener(null);
                        leaderboardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Bundle bundle = new Bundle();
                                LeaderboardEntry entry = leaderboardAdapter.getItem(position);
                                bundle.putString("QRID", entry.getId());
                                bundle.putBoolean("isUser", false);
                                Navigation.findNavController(view).navigate(R.id.action_nav_leaderboard_to_nav_QRDetail, bundle);
                            }
                        });

                        // Changes leaderboard if location data is not enabled
                        dataList = data.getTopRegionQRPoints();
                        if (city == null) {
                            leaderboardPlayerTitle.setText("My Best QRMon");
                            QRCode qr = profile.getBestQR();

                            if (qr == null) {
                                myName.setText("N/A");
                                myPoints.setText("N/A");

                            } else {
                                myName.setText(qr.getName());
                                myPoints.setText(Integer.toString(qr.getScore()));
                            }
                            myRankNumber.setText("N/A");

                            noRegionText.setVisibility(View.VISIBLE);
                            leaderboardAdapter.clear();
                            leaderboardAdapter.notifyDataSetChanged();
                            leaderboardType.setText("Top QRMons in Region");
                        } else {
                            // Fills leaderboard data with correct type
                            leaderboardPlayerTitle.setText("My Best QRMon from " + city);

                            QRCode qr = profile.getBestQR();
                            if (qr == null) {
                                myName.setText("N/A");
                                myPoints.setText("N/A");
                                myRankNumber.setText("N/A");
                            } else {
                                // Checks rank of best QR in the region, if any
                                int myRank = 0;
                                boolean hasFlag = false;
                                while (myRank < dataList.size()) {
                                    for (QRCode checkQR : profile.getCaptured().values()) {
                                        if (checkQR.getIdHash().equals(dataList.get(myRank).getId())) {
                                            hasFlag = true;
                                            myName.setText(checkQR.getName());
                                            myPoints.setText(Integer.toString(checkQR.getScore()));
                                            myRankNumber.setText(Integer.toString(myRank + 1));
                                            break;
                                        }
                                    }
                                    if (hasFlag) {
                                        break;
                                    }
                                    myRank += 1;
                                }

                                if (!hasFlag) {
                                    myName.setText("N/A");
                                    myPoints.setText("N/A");
                                    myRankNumber.setText("N/A");
                                }
                            }
                            leaderboardType.setText("Top QRMons in " + city);

                            noRegionText.setVisibility(View.GONE);
                            leaderboardAdapter.clear();
                            leaderboardAdapter.addAll(data.getTopRegionQRPoints());
                            leaderboardAdapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private String getCurrCity() {
        // get reference to the LocationManager
        LocationManager locationManager = (LocationManager) requireActivity()
                .getSystemService(getActivity().LOCATION_SERVICE);
        // check if the app has permission to access the device's location
        if (ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // if not, request permission
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        // get the current location (required to check for permissions again)
        double currLongitude = 0;
        double currLatitude = 0;
        if (ContextCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0L, (float) 0, (LocationListener) FragmentLeaderboard.this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                currLongitude = location.getLongitude();
                currLatitude = location.getLatitude();
            } else {
                currLongitude = 0;
                currLatitude = 0;
            }
        }
        // get the city name from longitude and latitude
        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(currLatitude, currLongitude, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            if (address.getLocality() != null) {
                return address.getLocality();
            }
        }
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}