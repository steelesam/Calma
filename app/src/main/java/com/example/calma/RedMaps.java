package com.example.calma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference sendersName;
    private FirebaseAuth mAuth;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final long MIN_TIME = 1000;
    private final long MIN_DISTANCE = 5;
    private static final int REQUEST_CODE = 101;
    private LatLng latLng;
    private Button hospitals, policeStations, safeSpots;
    private int ProximityRadius = 10000;
    ArrayList<String> listOfSafePlaces = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        hospitals = findViewById(R.id.viewHospitalsRed);
        hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHospitals(hospitals);
            }
        });
        policeStations = findViewById(R.id.viewPoliceStationsRed);
        policeStations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoliceStations(policeStations);
            }
        });


        safeSpots = findViewById(R.id.viewSafePlacesRed);
        safeSpots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String place : listOfSafePlaces) {
                    showSafeSpotsOnMap(getApplicationContext(), place);
                }


            }
        });

    }

    /**
     * Fetches most recent location using fusedLocationClient and applies result to currentLocation object
     * which is then used in onMapReady to apply markers to screen
     */
    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    fetchLastLocation();
                    // Test marker set in central London
                    //latLng = new LatLng(51.5159,-0.1361);
                    latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.2f));

                    // Loading up list of safe addresses to arrayList for future use
                    getSafePlaceAddressesFromDatabase();

                    // Retrieves HashMap from traffic lights activity and checks if phone number
                    // of selected spinner option is equal to one in contacts list
                    Intent intent = getIntent();
                    Map<String, String> phoneMap = (HashMap<String, String>) intent.getSerializableExtra("phoneMap");
                    String phoneNumber = "";
                    Bundle bundle = getIntent().getExtras();
                    final String selectedName = bundle.get("spinnerName").toString();
                    for (Map.Entry<String, String> entry : phoneMap.entrySet()) {
                        String name = entry.getKey();
                        if (name.equals(selectedName)) {
                            phoneNumber = entry.getValue();
                        }
                    }

                    final String myLatitude = String.valueOf(currentLocation.getLatitude());
                    final String myLongitude = String.valueOf(currentLocation.getLongitude());
                    long time = System.currentTimeMillis();
                    final String timeSent = String.valueOf(time);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    final String strDate = dateFormat.format(date);


                    // Adds the latitude and longitude of currentLocation, time it was sent,
                    // date it was sent and recipient to Firebase Database for future use
                    RedAlert redAlert = new RedAlert(myLongitude, myLatitude, timeSent, strDate, selectedName);
                    addRedAlertToDb(redAlert);



                    // Sends text containing google maps link to designated phone number/contact
                    sendersName = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                    Query query = sendersName.orderByChild("name");
                    final String amberPhoneNumber = phoneNumber;
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            String message = "You have received a RED Calma alert from: " + name + ".\n" +
                                    "https://www.google.com/maps?q=" + myLatitude + "," + myLongitude + "\n";
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(amberPhoneNumber, null, message, null, null);
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        /**
         * Determines whether to use Network provider or GPS signal to get location updates
         */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //assert locationManager != null;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location services must be enabled to use this service.", Toast.LENGTH_LONG).show();
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);
        }


    }

    /**
     * If location service permissions are granted then proceed and fetch location of user
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    /**
     * Pushes red alert object to database
     * @param redAlert
     */
    public void addRedAlertToDb(RedAlert redAlert){
        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Red Alert").push()
                .setValue(redAlert).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(RedMaps.this, "Red alert sent", Toast.LENGTH_SHORT).show();
                    openRed();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                } else {
                    Toast.makeText(RedMaps.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * Reopens map to fix bug where map crashes
     */
    private void openRed() {
        Intent intent = new Intent(this, RedMaps.class);

        startActivity(intent);
    }


    /**
     * Shows hospitals in X radius
     */
    public void showHospitals(View view) {
        String hospital = "hospital";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        String url = getUrl(currentLocation.getLatitude(), currentLocation.getLongitude(), hospital);
        transferData[0] = mMap;
        transferData[1] = url;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        getNearbyPlaces.execute(transferData);
        Toast.makeText(this, "Searching for nearby hospitals", Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows police stations in X radius
     */
    public void showPoliceStations(View view) {
        String policeStation = "police";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        String url = getUrl(currentLocation.getLatitude(), currentLocation.getLongitude(), policeStation);
        transferData[0] = mMap;
        transferData[1] = url;
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
        getNearbyPlaces.execute(transferData);
        Toast.makeText(this, "Searching for nearby police stations", Toast.LENGTH_SHORT).show();

    }

    /**
     * Appends string segments used to send Places API request
     *
     * @param latitude
     * @param longitude
     * @param nearbyPlace
     * @return
     */
    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyC4sWRF0S7Zk9VBSWOF8zlEjY_EDg4eM1I");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    /**
     * Gets the registered safe place address of user to then be accessed for use in GeoCoding to formulate marker on map
     */
    public void getSafePlaceAddressesFromDatabase() {
        DatabaseReference getSafePlaces = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mAuth.getCurrentUser().getUid()).child("Safe Places");
        getSafePlaces.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String address = dataSnapshot.getValue(SafePlace.class).toString();
                listOfSafePlaces.add(address);
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * On click of safeSpot button, markers are shown on map. Addresses are taken from database and added to arrayList then GeoCoded from this method
     * to provide Lat and Long so as to provide coordinates for marker placement
     *
     * @param
     */
    public LatLng showSafeSpotsOnMap(Context context, String strAddress) {

        // TODO: Test properly with working map taking addresses from db
        // TEST
        //1842 N Shoreline Blvd, Mountain View, CA 94043, United States

        Geocoder geocoder = new Geocoder(context);
        List<Address> address;
        LatLng point = null;



        try {

            address = geocoder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }


            Address location = address.get(0);
            {
                point = new LatLng(location.getLatitude(), location.getLongitude() );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(point).title("Safe Spot"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10.2f));
        return point;


    }


}