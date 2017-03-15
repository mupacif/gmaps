package be.formation.gmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    GoogleMap gm;
    Boolean ready = false;
    GoogleApiClient mgac;
    Location loc;
    LocationRequest lreq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mgac = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        Log.i(TAG,(mgac!=null)+"/gmac");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        ready = true;
        LatLng ll = new LatLng(50,4);
        CameraPosition target= CameraPosition.builder().target(ll).zoom(19).tilt(15).build();
        gm.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        gm.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        System.out.println("heello");
        gm.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                gm.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .position(latLng));

            }
        });




    }
    public void flyTo(Location loc)
    {
        if(ready && loc!=null)
        {
            LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());
            CameraPosition target= CameraPosition.builder().target(ll).zoom(14).build();
            gm.animateCamera(CameraUpdateFactory.newCameraPosition(target));
        }

    }

    private synchronized void buildGoogleClientApi() {

        mgac = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mgac.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mgac.isConnected())
            mgac.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        lreq = LocationRequest.create();
        lreq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        lreq.setInterval(1000);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mgac,lreq,this);
            // Si on a pas encore la permission mais qu'on est sur une API >= 23
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },1);
        }






        Log.i(TAG,"connected");
        loc = LocationServices.FusedLocationApi.getLastLocation(mgac);
        Log.i(TAG,"pos:"+loc);
        flyTo(loc);
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i(TAG,"disconnected");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"connection failed");

    }

    @Override
    public void onLocationChanged(Location location) {
      loc = location;
        flyTo(location);
    }
}
