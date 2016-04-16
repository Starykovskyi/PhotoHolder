package com.starikovskiy_cr.scope_8.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.starikovskiy_cr.scope_8.R;
import com.starikovskiy_cr.scope_8.callbacks.IPhotoPreviewListener;
import com.starikovskiy_cr.scope_8.db.dao.DAOFactory;
import com.starikovskiy_cr.scope_8.db.models.IPhoto;
import com.starikovskiy_cr.scope_8.loaders.PhotoWithCoordinatesLoader;
import com.starikovskiy_cr.scope_8.net.MyInfoWindowAdapter;
import com.starikovskiy_cr.scope_8.view.adapter.PlaceArrayAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by starikovskiy_cr on 04.03.16.
 */
public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<IPhoto>>, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks {
    static Map<String, IPhoto> photoMarkers = new HashMap<>();
    static String TAG = "lol";
    private static int LOADER_ID = 6666;
    protected GoogleApiClient mGoogleApiClient;

    @Bind(R.id.autocomplete_places)
    AutoCompleteTextView mAutocompleteView;

    private GoogleMap mMap;
    private SupportMapFragment instance = SupportMapFragment.newInstance();
    private IPhotoPreviewListener previewListener;
    private PlaceArrayAdapter mAdapter;
    LatLng currentSelection;

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback  = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, root);
        if (savedInstanceState == null) {
            getChildFragmentManager().
                    beginTransaction().
                    replace(R.id.map_container, instance)
                    .commit();
        }

        previewListener = (IPhotoPreviewListener) getActivity();
        instance.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                getLoaderManager().restartLoader(LOADER_ID, null, MapFragment.this);
            }
        });
        mAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1, null, null);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        return root;
    }

    private void initMarkers(List<IPhoto> data) {
        for (IPhoto photo : data) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(photo.getLatitude(), photo.getLongitude())));
            photoMarkers.put(marker.getId(), photo);
        }
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getContext(), photoMarkers));
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public Loader<List<IPhoto>> onCreateLoader(int id, Bundle args) {
        return new PhotoWithCoordinatesLoader(DAOFactory.getPhotoDAO(), getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<IPhoto>> loader, List<IPhoto> data) {
        initMarkers(data);
    }

    @Override
    public void onLoaderReset(Loader<List<IPhoto>> loader) {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        previewListener.previewPhoto(photoMarkers.get(marker.getId()).getId());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mAdapter.setGoogleApiClient(null);
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

}
