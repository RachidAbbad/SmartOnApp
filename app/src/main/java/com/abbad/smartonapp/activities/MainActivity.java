package com.abbad.smartonapp.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.ui.notifications.NotificationsFragment;
import com.abbad.smartonapp.ui.profile.ProfileFragment;
import com.abbad.smartonapp.utils.TapTargetGuide;
import com.abbad.smartonapp.utils.WebServiceConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final Fragment fragment1 = new DashboardFragment();
    final Fragment fragment2 = new InterventionFragment();
    final Fragment fragment3 = new NotificationsFragment();
    final Fragment fragment4 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private TextView cnxStatus;
    private CardView cnxStatusColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        cnxStatus = findViewById(R.id.cnxStatus);
        cnxStatusColor = findViewById(R.id.cnxStatusColor);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Log.i("ChekingCnxMain","ChekingConnectionMainActivity");
                if(!WebServiceConnection.isNetworkAvailable(getApplicationContext())){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cnxStatus.setText(getResources().getString(R.string.cnxStatusMsgOffline));
                            cnxStatusColor.setCardBackgroundColor(getResources().getColor(R.color.uiRed));

                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cnxStatus.setText(getResources().getString(R.string.cnxStatusMsgOnline));
                            cnxStatusColor.setCardBackgroundColor(getResources().getColor(R.color.uiGreen));
                        }
                    });
                }
            }
        },0, 1500);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_dashboard,R.id.navigation_intervention, R.id.navigation_notifications,R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
  //      NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //new TapTargetGuide(navView,this).firstGuide();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_intervention:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_notifications:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;

                case R.id.navigation_profile:
                    fm.beginTransaction().hide(active).show(fragment4).commit();
                    active = fragment4;
                    return true;
            };
            return false;
        }
    };
}