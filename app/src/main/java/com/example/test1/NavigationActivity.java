package com.example.test1;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.util.MarkerIcons;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;
import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;
import static com.naver.maps.map.CameraUpdate.REASON_CONTROL;
import static com.naver.maps.map.CameraUpdate.REASON_GESTURE;
import static com.naver.maps.map.CameraUpdate.REASON_LOCATION;

public class NavigationActivity extends HideBottomBarActivity implements LocationListener, OnMapReadyCallback {

    private FragmentManager fm;
    private MenuFragment menuFragment;
    private RegisterListFragment registerListFragment;
    private RecommendListFragment recommendListFragment;
    private FragmentTransaction ft;
    private BottomNavigationView bottomNavigationView;
    private MapFragment mapFragment;
    private NaverMap map;
    private LocationManager locationManager;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FloatingActionButton floatingActionButton;
    private UiSettings uiSettings;
    private FusedLocationSource locationSource;
    private GeofencingClient geofencingClient;
    private ArrayList<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent = null;
    private boolean geofenceListCompleteCheck = false;
    private BackPressHandler backPressHandler;
    private LottieAnimationView animation_geofence;
    private boolean isInBoundGeofence;
    private boolean isEventDialogAlreadyShown = false;
    private String currentGeofence = null;
    private String selectedLocation = null;
    private String selectedRecommendItem = null;
    private Double selectedRecommenditemLatitude = null;
    private Double selectedRecommenditemLongitude = null;
    private boolean isMapInitializeCheck = false;

    private static final String TAG = "NavigationActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    private Map<String, GeoPoint> locationData = new HashMap<>();
    private ArrayList<RecommendListData> list = new ArrayList<>();
    private GeoPoint geoPoint;
    private int[] arrSrc = {R.drawable.park1, R.drawable.park2, R.drawable.park3, R.drawable.park4, R.drawable.park5, R.drawable.park6, R.drawable.park7};
    private String[] arrName = {"???????????????????????????", "????????????", "????????????", "??????????????????"
            , "??????????????????", "?????????", "??????????????????" };

//    ?????????????????? ??????????????? ??????(?????????????????? ??????/??????/??????)??? ???????????? ???????????? FragmentTrasaction?????? ????????? API??? ???????????? ?????????.
//    FragmentTrasaction ????????? ?????? ???????????? FragmentManager ?????? ????????? ???????????????.
//    ?????? ????????? ?????? ???????????? getFragmentManager() ?????? ????????? ?????? ????????? ??? ????????????.
//
//    ??????????????? FragmentTrasaction ?????? ????????? ?????? ???????????? FragmentManager??? beginTransaction() ????????? ?????? ?????? ??? ????????????.

    //    FragmentTrasaction ??? replace() ????????? ?????? ?????????????????? ???????????? ??? ???????????????.
//    ??? ?????? ????????? ??????????????? ???????????? ????????? ???????????? ?????????????????? ????????? ????????? ViewGroup?????????.
//    ???????????? ????????? ID?????? ????????? ??? ??????????????? FrameLayout??? ???????????????. ??? ?????? ????????? ??????????????? ?????? Fragment ???????????????.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        hideNavigationBar();

        for(int i = 0; i < Array.getLength(arrSrc); i++) {
            RecommendListData data = new RecommendListData();
            data.setTitle(arrName[i]);
            data.setImgSrc(arrSrc[i]);
            list.add(data);
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        animation_geofence = findViewById(R.id.animation_geofence);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        mapFragment = (MapFragment) fm.findFragmentById(R.id.map_layout);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        geofencingClient = LocationServices.getGeofencingClient(this);
        backPressHandler = new BackPressHandler(this);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }

        //??? ?????? API??? ??????
        mapFragment.getMapAsync(NavigationActivity.this::onMapReady);
        ft.replace(R.id.main_layout, mapFragment).commit();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //?????? ??????????????? ??? ????????? ?????? ?????????
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //ft = fm.beginTransaction(); // !! ????????? fm??? ?????? beginTransaction() ???????????? ?????? FragmentTransaction ?????? ????????? ???????????? ????????? ?????????
                switch (menuItem.getItemId()) {
                    case R.id.tab1: {

                        if (mapFragment == null) {
                            geofenceList.clear();
                            fm.beginTransaction().add(R.id.main_layout, mapFragment).commit();
                            mapFragment.getMapAsync(NavigationActivity.this::onMapReady);

                        }
                        if (mapFragment != null) fm.beginTransaction().show(mapFragment).commit();
                        if (recommendListFragment != null)
                            fm.beginTransaction().hide(recommendListFragment).commit();
                        if (menuFragment != null) fm.beginTransaction().hide(menuFragment).commit();
                        if (registerListFragment != null)
                            fm.beginTransaction().hide(registerListFragment).commit();

                        //??? ?????? API??? ??????
                        return true;
                    }
                    case R.id.tab2: {
                        if (recommendListFragment == null) {
                            recommendListFragment = new RecommendListFragment();
                            fm.beginTransaction().add(R.id.main_layout, recommendListFragment).commit();
                        }
                        if (mapFragment != null) fm.beginTransaction().hide(mapFragment).commit();
                        if (recommendListFragment != null)
                            fm.beginTransaction().show(recommendListFragment).commit();
                        if (menuFragment != null) fm.beginTransaction().hide(menuFragment).commit();
                        if (registerListFragment != null)
                            fm.beginTransaction().hide(registerListFragment).commit();

                        return true;
                    }

//                    case R.id.tab3: {
//                        if (menuFragment == null) {
//                            menuFragment = new MenuFragment();
//                            fm.beginTransaction().add(R.id.main_layout, menuFragment).commit();
//                        }
//                        if (mapFragment != null) fm.beginTransaction().hide(mapFragment).commit();
//                        if (recommendListFragment != null)
//                            fm.beginTransaction().hide(recommendListFragment).commit();
//                        if (menuFragment != null) fm.beginTransaction().show(menuFragment).commit();
//                        if (registerListFragment != null)
//                            fm.beginTransaction().hide(registerListFragment).commit();
//
//                        return true;
//                    }

                    case R.id.tab4: {
                        if (registerListFragment == null) {
                            registerListFragment = new RegisterListFragment();
                            fm.beginTransaction().add(R.id.main_layout, registerListFragment).commit();
                        }
                        if (mapFragment != null) fm.beginTransaction().hide(mapFragment).commit();
                        if (recommendListFragment != null)
                            fm.beginTransaction().hide(recommendListFragment).commit();
                        if (menuFragment != null) fm.beginTransaction().hide(menuFragment).commit();
                        if (registerListFragment != null)
                            fm.beginTransaction().show(registerListFragment).commit();
                        return true;
                    }

                }
                return false;
            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {
            if (intent.getStringExtra("type").equals("MyInEvent")) {
                currentGeofence = intent.getStringExtra("placeName");
                isInBoundGeofence = intent.getBooleanExtra("check", false);
                Toast.makeText(this, "return received : " + isInBoundGeofence, Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (isInBoundGeofence == true && isEventDialogAlreadyShown == false) {
                                    try {
                                        GeofenceEventDialog dlg = new GeofenceEventDialog(NavigationActivity.this, R.layout.fragment_geofence_dialog);
                                        dlg.show();
                                        isEventDialogAlreadyShown = true;
                                    } catch (Exception e) {
                                        Log.d("myTag", "dlg error : ", e);
                                    }

                                }

                            }
                        });
                    }
                }).start();

                if (isInBoundGeofence == false) {
                    isEventDialogAlreadyShown = false;
                }

            } else if (intent.getStringExtra("type").equals("OtherPeopleInEvent")) {
                Log.d("myTag", "otherpeoplein");
            }
            else if(intent.getStringExtra("type").equals("rec_list_item_click")) {
                fm.beginTransaction().hide(recommendListFragment).commit();
                fm.beginTransaction().show(mapFragment).commit();
                selectedRecommendItem = intent.getStringExtra("fName");
                selectedRecommenditemLatitude = intent.getDoubleExtra("latitude", 0);
                selectedRecommenditemLongitude = intent.getDoubleExtra("longitude", 0);
                mapFragment.getMapAsync(this);
            }

        }
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }

    private boolean hasPermission() {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0])
                == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, PERMISSIONS[1])
                == PermissionChecker.PERMISSION_GRANTED
                && PermissionChecker.checkSelfPermission(this, PERMISSIONS[2])
                == PermissionChecker.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (hasPermission() && locationManager != null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 10, this);
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        super.onStart();


        // ???????????? ?????? ??? ?????? ???????????? ?????? ??????
        if (hasPermission()) {
            if (locationManager != null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 10, this);

            }
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }



    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        //????????? ?????????????????? ????????? ??????

        if (isInBoundGeofence == true) {
            db.collection("facility").document(currentGeofence)
                    .update("nInboundPeople", FieldValue.increment(-1));
        }

        geofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences removed
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });


    }


    @Override
    public void onLocationChanged(Location location) {
        if (map == null || location == null) {
            return;
        }

        LatLng coord = new LatLng(location);

        LocationOverlay locationOverlay = map.getLocationOverlay();
        locationOverlay.setVisible(true);
        locationOverlay.setPosition(coord);
        locationOverlay.setBearing(location.getBearing());

        map.moveCamera(CameraUpdate.scrollTo(coord));
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

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        CircleOverlay circle = new CircleOverlay();


    Marker marker = new Marker();
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_BICYCLE,true);
        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_MOUNTAIN,true);
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

    //Android 4.0 ?????? ????????? ??????????????? ????????? ??? ????????? Thread ???????????? ???. networkOnMainthreadException ??????
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                Map<Integer, String[]> data = myXmlDataParsing();
//                //UI Thread(Main Thread)??? ????????? ?????? Thread??? ????????? ????????? ??? ???????????????
//                //runOnUiThread()??? ???????????? UI Thread??? TextView ?????? ??????????????? ???
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//
//                        Iterator<Integer> keys = data.keySet().iterator();
//                        while (keys.hasNext()) {
//                            int key = keys.next();
//                            String[] value = data.get(key);
//                            FacilityData faciData = new FacilityData();
//                            faciData.setnInboundPeople(0);
//                            faciData.setfName(value[0]);
//                            faciData.setfRoadAddr(value[1]);
//                            faciData.setFgeoPoint(new GeoPoint(Double.parseDouble(value[2]), Double.parseDouble(value[3])));
//                            faciData.setfTypeCd(value[4]);
//                            faciData.setfCodNm(value[5]);
//
////                            data.put(count, new String[]{mName, mAddr, mLatitude, mLongitude, mTypeCd, mCobNm, "0"});
//
//                            db.collection("facility").document(value[0]).set(faciData)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            //
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.d("parsing", "db upload failed");
//                                        }
//                                    });
//
//                        }
//                    }
//                });
//            }
//        }).start();
    uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);
        uiSettings.setZoomControlEnabled(false);

        if(selectedRecommendItem != null) {
            CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(
                    new LatLng(selectedRecommenditemLatitude, selectedRecommenditemLongitude), 15)
                    .animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);
        }

        if(isInBoundGeofence != false) {

        }

        if(isMapInitializeCheck != true) {
//            new Thread(new Runnable() {
//                @Override
//                public void run () {
//                    db.collection("registlist")
//                            .get()
//                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        for (QueryDocumentSnapshot document : task.getResult()) {
//                                            Marker db_marker = new Marker();
//                                            LatLng coord = new LatLng
//                                                    (document.getGeoPoint("geoPoint").getLatitude()
//                                                            , document.getGeoPoint("geoPoint").getLongitude());
//                                            db_marker.setPosition(coord);
//                                            db_marker.setMap(naverMap);
//                                            db_marker.setTag(document.get("title") + "\n" + document.get("type"));
//
//                                            InfoWindow infoWindow = new InfoWindow();
//                                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
//                                                @NonNull
//                                                @Override
//                                                public CharSequence getText(@NonNull InfoWindow infoWindow) {
//                                                    return (CharSequence) infoWindow.getMarker().getTag();
//                                                }
//                                            });
//
//                                            db_marker.setOnClickListener(overlay -> {
//                                                Toast.makeText(NavigationActivity.this, "db_marker click", Toast.LENGTH_SHORT).show();
//                                                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(coord, 15)
//                                                        .animate(CameraAnimation.Easing);
//                                                naverMap.moveCamera(cameraUpdate);
//                                                infoWindow.open(db_marker);
//                                                return true;
//                                            });
//
//                                            naverMap.addOnCameraChangeListener((reason, animated) -> {
//                                                if (reason == REASON_GESTURE || reason == REASON_CONTROL || reason == REASON_LOCATION)
//                                                    infoWindow.close();
//                                            });
//
//                                            infoWindow.setOnClickListener(overlay -> {
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
//                                                builder.setTitle(document.get("title").toString())
//                                                        .setMessage("   ")
//                                                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                            }
//                                                        })
//                                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialog, int which) {
//
//                                                            }
//                                                        });
//                                                if (user.getUid().equals(document.get("registUid"))) {
//                                                    builder.setNeutralButton("??????", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//                                                        }
//                                                    });
//
//                                                }
//                                                builder.create().show();
//                                                return true;
//                                            });
//                                        }
//                                    }
//                                }
//                            });
//                }
//            }).start();
//            //db??? ?????? ?????? ???????????? ?????? ??????

            new Thread(new Runnable() {
                @Override
                public void run () {
                    //?????? ?????? ?????? ??????
                    geofenceList.clear();
                    db.collection("facility").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            Marker faci_marker = new Marker();
                                            faci_marker.setWidth(Marker.SIZE_AUTO);
                                            faci_marker.setHeight(Marker.SIZE_AUTO);
                                            faci_marker.setIcon(MarkerIcons.BLACK);
                                            faci_marker.setIconTintColor(Color.parseColor("#64CD3C"));
                                            faci_marker.setTag(document.get("fName") + "\n" + document.get("fCodNm"));

                                            InfoWindow infoWindow = new InfoWindow();
                                            infoWindow.setAdapter(new InfoWindow.DefaultTextAdapter(getApplicationContext()) {
                                                @NonNull
                                                @Override
                                                public CharSequence getText(@NonNull InfoWindow infoWindow) {
                                                    return (CharSequence) infoWindow.getMarker().getTag();
                                                }
                                            });


                                            LatLng loc = new LatLng(document.getGeoPoint("fgeoPoint").getLatitude()
                                                    , document.getGeoPoint("fgeoPoint").getLongitude());
                                            faci_marker.setPosition(loc);
                                            faci_marker.setMap(naverMap);

                                            // ?????? ?????? ?????????, ????????? ????????? ?????? ????????? ????????? ??????, ????????? ??????
                                            faci_marker.setOnClickListener(overlay -> {
                                                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(loc, 15)
                                                        .animate(CameraAnimation.Easing);
                                                naverMap.moveCamera(cameraUpdate);
                                                infoWindow.open(faci_marker);
                                                Toast.makeText(NavigationActivity.this,
                                                        loc.latitude + "," + loc.longitude
                                                        , Toast.LENGTH_SHORT).show();
                                                return true;
                                            });


                                            // ??????????????????????????? ???????????? ???????????? ???????????? ????????????
                                            naverMap.addOnCameraChangeListener((reason, animated) -> {
                                                if (reason == REASON_GESTURE || reason == REASON_CONTROL || reason == REASON_LOCATION)
                                                    infoWindow.close();
                                            });

                                            // ????????? ????????? ????????? ???????????? ?????? ?????? ?????????
                                            // ?????? ?????????????????? ??????
                                            infoWindow.setOnClickListener(overlay -> {
                                                RegisterDialog dlg = new RegisterDialog(NavigationActivity.this, R.layout.register_dialog);
                                                RegisterData data = new RegisterData();

                                                dlg.btn_register.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dlg.reg_count = dlg.edit_people_number.getText().toString();
                                                        dlg.reg_title = dlg.edit_title.getText().toString();
                                                        dlg.reg_date = dlg.text_date.getText().toString();
                                                        dlg.reg_time = dlg.text_time.getText().toString();
                                                        dlg.reg_type = dlg.edit_type.getText().toString();

                                                        if (dlg.edit_people_number.getText().toString().isEmpty()
                                                                || dlg.edit_type.getText().toString().isEmpty()
                                                                || dlg.text_time.getText().toString().equals("????????? ??????????????????:")
                                                                || dlg.reg_title == null)
                                                            Toast.makeText(NavigationActivity.this, "???????????? ?????? ?????? ????????????", Toast.LENGTH_SHORT).show();
                                                        else {
                                                            selectedLocation = document.get("fName").toString();

                                                            data.setCount(Integer.parseInt(dlg.reg_count));
                                                            data.setDate(dlg.reg_date);
                                                            data.setTime(dlg.reg_time);
                                                            data.setTitle(dlg.reg_title);
                                                            data.setType(dlg.reg_type);
                                                            data.setfName(selectedLocation);
                                                            data.setUid(user.getUid());
                                                            data.setCur_count(1);

                                                            Log.d("myTag", "" + data.getDate() + data.getTime() + data.getType());
                                                            dlg.show();
                                                            db.collection("facilityRegisterList").document(user.getUid() + selectedLocation).set(data)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("myTag", "database error", e);
                                                                        }
                                                                    });
                                                            db.collection("facilityRegisterList").document(user.getUid()+selectedLocation)
                                                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                                            if (e != null) {
                                                                                Log.d("myTag", "listner:error", e);
                                                                            }

                                                                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                                                                Toast.makeText(getApplicationContext(), "????????? ?????? ?????? : " +
                                                                                        documentSnapshot.getData().get("curCount"), Toast.LENGTH_SHORT).show();


                                                                            }
                                                                        }
                                                                    });
                                                            dlg.dismiss();
                                                        }

                                                    }
                                                });
                                                dlg.show();
                                                return true;
                                            });

                                            //???????????? ??????
                                            geofenceList.add(new Geofence.Builder()
                                                    // Set the request ID of the geofence. This is a string to identify this
                                                    // geofence.
                                                    .setRequestId(document.get("fName").toString())

                                                    .setCircularRegion(
                                                            document.getGeoPoint("fgeoPoint").getLatitude(),
                                                            document.getGeoPoint("fgeoPoint").getLongitude(),
                                                            500
                                                    )
                                                    .setExpirationDuration(NEVER_EXPIRE)
                                                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                                            Geofence.GEOFENCE_TRANSITION_EXIT)
                                                    .setLoiteringDelay(1000)
                                                    .setNotificationResponsiveness(2000)
                                                    .build());
                                            Log.d("myTag", "list size : " + geofenceList.size());
                                            Log.d("myTag", "after adding geofence code");
                                            if (geofenceList.size() > 60) {
                                                addGeofenceToClient();
                                                break;
                                            }

                                        }
                                    }
                                }
                            });
                }
            }).
                    start();

//
//            // ??? ?????? ?????????
//            naverMap.setOnMapClickListener((point,coord)->
//
//                    {
//                        geoPoint = new GeoPoint(coord.latitude, coord.longitude);
//                        marker.setPosition(coord);
//                        marker.setMap(naverMap);
//                    }
//            );
//
//            //?????? ?????? ?????????
//            marker.setOnClickListener(overlay ->
//
//                    {
//
//                        Intent intent = new Intent(getApplicationContext(), RegisteredListActivity.class);
//                        startActivity(intent);
//                        return true;
//
//                    }
//            );
//
//            //????????? ?????? ?????? ?????? ?????????
//            floatingActionButton.setOnClickListener(new View.OnClickListener()
//
//            {
//                @Override
//                public void onClick (View v){
//                    if (geoPoint == null) return;
//                    double latitude = geoPoint.getLatitude();
//                    double longitude = geoPoint.getLongitude();
//
//                    new RegisterDialog(NavigationActivity.this, R.layout.register_dialog)
//                            .show();
//                }
//            });
        }



            isMapInitializeCheck = true;

}

    //???????????? ?????? ????????? ??????
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }

    private void addGeofenceToClient() {
        try {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Geofences added
                            // ...
                            Log.d("myTag", "addGeofenceToClient success");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to add geofences
                            // ...
                            Log.d("myTag", "addGeofenceToClient Failed", e);
                        }
                    });

        } catch (Exception e) {
            Log.d("myTag", "" + e);
        }
    }


    //xml ????????? ??????
    private Map<Integer, String[]> myXmlDataParsing() {
        String mName = null, mLongitude = null, mLatitude = null, mAddr = null, mTypeCd = null, mCobNm = null;
        boolean inName = false, inLongitude = false, inLatitude = false, inAddr = false, inTypeCd = false, inCobNm = false;

        int count = 0;
        final String TAG = "parsing";
        Map<Integer, String[]> data = new HashMap<>();

        try {
            //?????? ?????????. ?????????????????? ??????????????? ??????, ?????? ??????????????? ?????????????????? ????????????
            URL url = new URL("http://www.kspo.or.kr/openapi/service/sportsFacilInfoService/getFacilInfoList?" +
                    "serviceKey=MwaC2oHs19ZNf5E8mGqrUcQ8gCYi2QdX95XqRkG5vA7d46B%2FG94Rsk8HHe3OunWA7%2BuRaVANYI52oRwUdqhVxg%3D%3D" +
                    "&numOfRows=200&pageNo=1&fmngCpNm=???????????????");

            //AndroidManifest??? application ???????????? android:usesCleartextTraffic??? true??? ??????????????? ?????? Http ????????? ????????? ??? ??????.
            //Cleartext HTTP traffic not permitted error!

            XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserFactory.newPullParser();

            parser.setInput(url.openStream(), null);
            int parserEvent = parser.getEventType();

            Log.d(TAG, "Parsing Start!");

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("faciNm"))
                            inName = true;
                        if (parser.getName().equals("faciRoadAddr1"))
                            inAddr = true;
                        if (parser.getName().equals("faciPointX"))
                            inLongitude = true;
                        if (parser.getName().equals("faciPointY"))
                            inLatitude = true;
                        if (parser.getName().equals("ftypeCd"))
                            inTypeCd = true;
                        if (parser.getName().equals("fcobNm"))
                            inCobNm = true;

                        break;

                    case XmlPullParser.TEXT:
                        if (inName) {
                            mName = parser.getText();
                            inName = false;
                        }
                        if (inAddr) {
                            mAddr = parser.getText();
                            inAddr = false;
                        }
                        if (inLatitude) {
                            mLatitude = parser.getText();
                            inLatitude = false;
                        }
                        if (inLongitude) {
                            mLongitude = parser.getText();
                            inLongitude = false;
                        }
                        if (inTypeCd) {
                            mTypeCd = parser.getText();
                            inTypeCd = false;
                        }
                        if (inCobNm) {
                            mCobNm = parser.getText();
                            inCobNm = false;
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            if (mLatitude.equals("-") || mLongitude.equals("-")) {
                                count++;
                                break;
                            }
                            FacilityData facilityData = new FacilityData();
                            data.put(count, new String[]{mName, mAddr, mLatitude, mLongitude, mTypeCd, mCobNm, "0"});

                            facilityData.setfName(mName);
                            facilityData.setFgeoPoint(new GeoPoint(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude)));
                            facilityData.setfRoadAddr(mAddr);
                            facilityData.setfTypeCd(mTypeCd);
                            facilityData.setfCodNm(mCobNm);
                            facilityData.setnInboundPeople(0);

                            Log.d(TAG, "????????? :" + mName);
                            Log.d(TAG, "????????? :" + mAddr);
                            Log.d(TAG, "?????? :" + mLatitude);
                            Log.d(TAG, "?????? :" + mLongitude);

                            count++;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
            Log.d(TAG, "failed!" + e);
        }
        return data;
    }
}


