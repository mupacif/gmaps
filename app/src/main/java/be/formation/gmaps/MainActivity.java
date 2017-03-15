package be.formation.gmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    static final String TAG="MainActivity";
    GoogleMap googleMap;
    Boolean mapReady = false;
    GoogleApiClient googleApi;
    Location position;

    //pour mettre à jour la position régulièrement
    LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // récupération du fragment
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //instantion de google api
        googleApi = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    // Quand la carte est prête , ca vient de  OnMapReadyCallback

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        mapReady = true;


        //la postion désiré
        LatLng pos = new LatLng(50,4);
        // la caméra
        CameraPosition target= CameraPosition.builder()
                .target(pos) // positon
                .zoom(19)  // niveau de zoom
                .tilt(15) // hauteur de la caméra
                .build();
        this.googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));

        //type de la caméra
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // gestion du click  pour mettre un marquer sur la carte
        this.googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MainActivity.this.googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .position(latLng));

            }
        });




    }
    // aller à une position
    public void flyTo(Location loc)
    {
        if(mapReady && loc!=null)
        {
            LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());
            CameraPosition target= CameraPosition.builder().target(ll).zoom(14).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(target));
        }

    }



    @Override
    protected void onStart() {
        super.onStart();
        googleApi.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(googleApi.isConnected())
            googleApi.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApi,locationRequest,this);
            // Si on a pas encore la permission mais qu'on est sur une API >= 23
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },1);
        }






        Log.i(TAG,"connected");
        position = LocationServices.FusedLocationApi.getLastLocation(googleApi);
        Log.i(TAG,"pos:"+position);
        flyTo(position);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"connection failed");
    }

    // appelé chaque fois que la position change 
    @Override
    public void onLocationChanged(Location location) {
      position = location;
        flyTo(location);
    }

}
