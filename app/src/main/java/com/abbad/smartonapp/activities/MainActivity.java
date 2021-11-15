package com.abbad.smartonapp.activities;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.abbad.smartonapp.R;
import com.abbad.smartonapp.datas.InterventionData;
import com.abbad.smartonapp.datas.ReportData;
import com.abbad.smartonapp.dialogs.AuthSiteDialog;
import com.abbad.smartonapp.dialogs.ComfirmExit;
import com.abbad.smartonapp.dialogs.LoadingBottomDialog;
import com.abbad.smartonapp.dialogs.ResultBottomDialog;
import com.abbad.smartonapp.ui.alerts.AlertsFragment;
import com.abbad.smartonapp.ui.dashboard.DashboardFragment;
import com.abbad.smartonapp.ui.interventions.InterventionFragment;
import com.abbad.smartonapp.ui.notifications.NotificationsFragment;
import com.abbad.smartonapp.ui.profile.ProfileFragment;
import com.abbad.smartonapp.utils.AuthAuSite;
import com.abbad.smartonapp.utils.Comun;
import com.abbad.smartonapp.utils.InterventionManager;
import com.abbad.smartonapp.utils.SessionManager;
import com.abbad.smartonapp.utils.TapTargetGuide;
import com.abbad.smartonapp.utils.WebServiceConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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

    public static AuthSiteDialog authSiteDialog;
    final Fragment fragment1 = new DashboardFragment();
    final Fragment fragment2 = new InterventionFragment();
    final Fragment fragment3 = new NotificationsFragment();
    final Fragment fragment4 = new AlertsFragment();
    final Fragment fragment5 = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    private TextView cnxStatus;
    private CardView cnxStatusColor;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    public static LoadingBottomDialog loadingBottomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        loadingBottomDialog = new LoadingBottomDialog("Loading ...");
        loadingBottomDialog.show(getSupportFragmentManager(),null);
        cnxStatus = findViewById(R.id.cnxStatus);
        cnxStatusColor = findViewById(R.id.cnxStatusColor);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Log.i("ChekingCnxMain", "ChekingConnectionMainActivity");
                if (!WebServiceConnection.isNetworkAvailable(getApplicationContext())) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cnxStatus.setText(getResources().getString(R.string.cnxStatusMsgOffline));
                            cnxStatusColor.setCardBackgroundColor(getResources().getColor(R.color.uiRed));
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cnxStatus.setText(getResources().getString(R.string.cnxStatusMsgOnline));
                            cnxStatusColor.setCardBackgroundColor(getResources().getColor(R.color.uiGreen));
                        }
                    });
                }
            }
        }, 0, 1500);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
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

                    case R.id.navigation_alerts:
                        fm.beginTransaction().hide(active).show(fragment4).commit();
                        active = fragment4;
                        return true;

                    case R.id.navigation_profile:
                        fm.beginTransaction().hide(active).show(fragment5).commit();
                        active = fragment5;
                        return true;
                }
                ;
                return false;
            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment5, "5").hide(fragment5).commit();


        new InterventionManager(getApplicationContext());
        try {
            //Initialise NfcAdapter
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);

            //If no NfcAdapter, display that the device has no NFC
            if (nfcAdapter == null) {
                Toast.makeText(this, "NO NFC Capabilities",
                        Toast.LENGTH_SHORT).show();
            }

            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            //new TapTargetGuide(navView,this).firstGuide();
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onBackPressed() {

            if (active == fragment1){
                new ComfirmExit().show(getSupportFragmentManager(),null);
            }
            else {
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
            }

    }

    public void enableNfcScan() {
        try {
            if (nfcAdapter != null) {
                nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
            }
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getSupportFragmentManager(), null);
        }
    }

    public void disableNfcScan() {
        try {
            if (nfcAdapter != null) {
                nfcAdapter.disableForegroundDispatch(this);
            }
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getSupportFragmentManager(), null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (authSiteDialog == null) {
                return;
            }
            new AuthAuSite(this, SessionManager.getUserId(getApplicationContext()), getDataFromTag(tag, intent), authSiteDialog, AuthSiteDialog.AUTH_NFC).execute();
            disableNfcScan();
        } catch (Exception ex) {
            new ResultBottomDialog(ex.getMessage(), 3).show(getSupportFragmentManager(), null);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(nfcAdapter != null){
            nfcAdapter.enableForegroundDispatch(this,pendingIntent,null,null);
        }
    }

    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    private String getDataFromTag(Tag tag, Intent intent) {

        Ndef ndef = Ndef.get(tag);
        try {
            ndef.connect();

            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();

                ndef.close();
                String info = new String(payload);
                Log.e("NFCString", info);
                return info;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
            return null;
        }
        return null;
    }

    //TODO start "pushNotificationsService" when this activity is destroyed
}