package com.example.google_map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "estilo de mapa";
    private static final int  REQUEST_LOCATION_PERMISSION = 1;

    private Spinner spinner_tipomapa;

    private String [] tipo_mapa;

    private String  tipo_mapa_escogido = "";



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_options , menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId())
       {
           case R.id.normal_map:
               mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
               break;
           case R.id.hybrid_map:
               mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
               break;
           case R.id.satellite_map:
               mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
               break;
           case R.id.terrain_map:
               mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
               break;
           default:
               return super.onOptionsItemSelected(item);
       }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mapFragment).commit();

        mapFragment.getMapAsync(this);

        spinner_tipomapa = (Spinner) findViewById(R.id.spinner_tipomapa);

        tipo_mapa = getResources().getStringArray(R.array.tipo_mapa);

        spinner_tipomapa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positiontipomapa, long l) {

                tipo_mapa_escogido = tipo_mapa[Integer.parseInt(String.valueOf(spinner_tipomapa.getItemIdAtPosition(positiontipomapa)))];

                Toast.makeText(MapsActivity.this, "escogio la opcion " + tipo_mapa_escogido,Toast.LENGTH_LONG).show();

                if(tipo_mapa_escogido == "Normal")
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                if(tipo_mapa_escogido == "Satelite")
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                if(tipo_mapa_escogido == "Hibrido")
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                if(tipo_mapa_escogido == "Terrarian")
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        float zoom = 16 ;

        try {
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext() ,R.raw.map_style));
            if(!success)
            {
                Log.e(TAG,"fallo al cargar el estilo del mapa");
            }
        }
        catch (Resources.NotFoundException e)
        {
            Log.e(TAG,"No es posible hallar el estilo . ERROR", e);
        }

        // Add a marker in Sydney and move the camera
        LatLng tacna = new LatLng(-18.0140483, -70.2533381);
        mMap.addMarker(new MarkerOptions().position(tacna).title("Marker in Tacna"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tacna,zoom));

        GroundOverlayOptions iconoverlay = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.home)).position(tacna,100);

        mMap.addGroundOverlay(iconoverlay);

        setMapLongClick(mMap);
        setPoiClick(mMap);
        enableMyLocation();
        setInfoWindowClickPanorama(mMap);
    }

    private void setMapLongClick(final GoogleMap map)
    {
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                String snippter = String.format(Locale.getDefault(), "Lat: %1$.5f, Long: %2$.5f" ,latLng.latitude, latLng.longitude);

                map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).position(latLng).title(getString(R.string.app_name)).snippet(snippter)) ;

               // map.addMarker(new MarkerOptions().position(latLng)) ;
            }
        });
    }

    //paara poner un marcador
    private void setPoiClick(final GoogleMap map)
    {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest pointOfInterest) {
                Marker poiMarker = map.addMarker(new MarkerOptions().position(pointOfInterest.latLng).title(pointOfInterest.name));

                poiMarker.setTag("poi");
                poiMarker.showInfoWindow();
            }
        });
    }

    private void enableMyLocation()
    {
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);

        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    enableMyLocation();
                }
        }
    }

    private void setInfoWindowClickPanorama(GoogleMap map )
    {
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTag() == "poi")
                {
                    StreetViewPanoramaOptions options = new StreetViewPanoramaOptions().position(marker.getPosition());

                    SupportStreetViewPanoramaFragment streetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance(options);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, streetViewPanoramaFragment).addToBackStack(null).commit();
                }
            }
        });
    }

}