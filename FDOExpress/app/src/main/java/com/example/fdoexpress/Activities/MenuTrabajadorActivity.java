package com.example.fdoexpress.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.fdoexpress.R;
import com.example.fdoexpress.Tasks.MyLocationServices;
import com.example.fdoexpress.Tasks.UpdateLocationTask;
import com.example.fdoexpress.Utils.Constantes;
import com.google.android.gms.location.*;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;

public class MenuTrabajadorActivity extends AppCompatActivity {
    static MenuTrabajadorActivity instance;
    private AppBarConfiguration mAppBarConfiguration;
    private int position_permission_code = 1;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest locationRequest;
    private SharedPreferences preferences;
    private Button button;



    public static MenuTrabajadorActivity getInstance(){
        return instance;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_trabajador);
        preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        instance =this;
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout_trabajador);
        final NavigationView navigationView = findViewById(R.id.nav_view_trabajador);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.trab_home, R.id.nav_orders_trabajador)
                .setDrawerLayout(drawer)
                .build();
        button = findViewById(R.id.btDesconectar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
                preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,false).apply();
                Intent intent   = new Intent(MenuTrabajadorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        NavController navController = Navigation.findNavController(this, R.id.nav_host_trabajador_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Dexter
                .withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                ))
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        for (PermissionGrantedResponse rp : report.getGrantedPermissionResponses()) {
                            switch (rp.getPermissionName()){
                                case Manifest.permission.ACCESS_FINE_LOCATION:
                                    updateLocation();
                                    break;
                            }
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }


    private void updateLocation() {
        buildLocationRequest();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, MyLocationServices.class);
        intent.setAction(MyLocationServices.ACTION_PROCESS_UPDATE);

        return PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest(){
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    public void updateLocate(String location){
        UpdateLocationTask updateLocationTask = new UpdateLocationTask(location+"&"+preferences.getString(Constantes.USER_CODE,""));
        updateLocationTask.execute();
        Toast.makeText(this, location, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                SharedPreferences preferences = getSharedPreferences(Constantes.STRING_PREFERENCES,MODE_PRIVATE);
                preferences.edit().putBoolean(Constantes.PREFERENCE_LOGIN_STATE,false).apply();

                Intent intent   = new Intent(MenuTrabajadorActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_trabajador_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Activity destruida","Destruida");
    }


}
