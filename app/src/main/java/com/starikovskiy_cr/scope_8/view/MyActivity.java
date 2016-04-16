package com.starikovskiy_cr.scope_8.view;

/**
 * Created by starikovskiy_cr on 12.03.16.
 */


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.callbacks.IGeolocationGetter;
import com.starikovskiy_cr.scope_8.callbacks.IPhotoHolder;
import com.starikovskiy_cr.scope_8.callbacks.IPhotoPreviewListener;
import com.starikovskiy_cr.scope_8.callbacks.ISynchronizeListener;
import com.starikovskiy_cr.scope_8.db.dao.DAOFactory;
import com.starikovskiy_cr.scope_8.db.dao.IPhotoDAO;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;
import com.starikovskiy_cr.scope_8.syncronizer.Synchronizer;
import com.starikovskiy_cr.scope_8.view.fragments.GalleryFragment;
import com.starikovskiy_cr.scope_8.view.fragments.MapFragment;
import com.starikovskiy_cr.scope_8.view.fragments.PhotoFragment;
import com.starikovskiy_cr.scope_8.view.fragments.PreviewFragment;

import java.util.LinkedList;
import java.util.Queue;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MyActivity extends AppCompatActivity implements IPhotoHolder,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        IPhotoPreviewListener,
        ISynchronizeListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static String PREFERENCES = "myPref";
    private static String SYNCHRONIZE = "synchronize";
    private static int PERMISSION_REQUEST = 666;
    @Bind(R.id.drawerlayoutgesamt)
    DrawerLayout drawerLayout;
    @Bind(R.id.navView)
    NavigationView navigationView;
    @Bind(R.id.activitylayout)
    FrameLayout activitylayout;

    private ActionBarDrawerToggle drawerToggle;
    private Queue<IPhoto> queue = new LinkedList<>();
    private IPhotoDAO dao = DAOFactory.getPhotoDAO();
    private SharedPreferences preferences;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private CharSequence mTitle;
    private IGeolocationGetter iGeolocationGetter;

    private MenuItem current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        ButterKnife.bind(this);
        drawerToggle = new ActionBarDrawerToggle(MyActivity.this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                return selectItem(menuItem);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.syncState();
        if (savedInstanceState == null) {
            current = navigationView.getMenu().getItem(1);
            selectItem(current);
            preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

            if (!preferences.getBoolean(SYNCHRONIZE, false)) {
                Synchronizer.synchronize(this);
            }
        }

        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    private boolean selectItem(MenuItem menuItem) {
        current = menuItem;
        switch (menuItem.getItemId()) {

            case R.id.drawerViewItem1: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                    Snackbar.make(activitylayout, "Have no permission", Snackbar.LENGTH_LONG)
                            .setAction("Make Permission",new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MyActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION},
                                            PackageManager.PERMISSION_GRANTED);
                                }
                            }).show();

                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activitylayout, PhotoFragment.newInstance())
                            .commit();
                }
                break;
            }

            case R.id.drawerViewItem2: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activitylayout, GalleryFragment.newInstance())
                        .commit();
                break;
            }
            case R.id.drawerViewItem3: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.activitylayout, MapFragment.newInstance())
                        .commit();
                break;

            }
        }
        drawerLayout.closeDrawers();
        menuItem.setChecked(true);
        mTitle = menuItem.getTitle();
        setTitle(mTitle);

        return false;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void addPhoto(IPhoto photo) {
        if (mLastLocation != null) {
            setCoordinates(photo);
        } else {
            queue.add(photo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(current == navigationView.getMenu().getItem(1)){
            selectItem(current);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                drawerToggle.setDrawerIndicatorEnabled(true);
                getSupportFragmentManager().popBackStack();
                setTitle(mTitle);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(new Configuration());
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (iGeolocationGetter != null) {
            iGeolocationGetter.onFail();
        }
        queue.clear();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            for (IPhoto iPhoto : queue) {
                setCoordinates(iPhoto);
            }
            if (iGeolocationGetter != null) {
                iGeolocationGetter.onGet();
            }
        } else {
            queue.clear();
        }

    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void previewPhoto(Long photoId) {
        drawerToggle.setDrawerIndicatorEnabled(false);
        PreviewFragment previewFragment = PreviewFragment.newInstance(photoId);

        iGeolocationGetter = previewFragment;

        setTitle("Preview");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activitylayout, previewFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setCoordinates(IPhoto photo) {
        photo.setLatitude(mLastLocation.getLatitude());
        photo.setLongitude(mLastLocation.getLongitude());
        dao.updateCortege(photo);
    }

    @Override
    public void onSynchronize() {
        navigationView.getMenu().getItem(1).setEnabled(true);
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean(SYNCHRONIZE, true);
        ed.commit();
    }
}
