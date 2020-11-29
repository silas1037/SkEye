package com.lavadip.skeye.config;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import com.lavadip.skeye.C0031R;
import com.lavadip.skeye.CustomDialog;
import com.lavadip.skeye.SkEye;
import com.lavadip.skeye.config.WorldMapView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AddLocationActivity extends Activity implements LocationListener {
    private static final int MSG_SUGGESTIONS_COMPUTED = 1;
    private static final int MSG_TAB_CHANGED = 0;
    public static final int REQUEST_ADD_LOCATION = 0;
    public static final int REQUEST_EDIT_LOCATION = 1;
    private static final int TABID_AUTO = 0;
    private Location coarseLocation;
    private TextView coarseStatusTxt = null;
    private final Set<String> coarseSuggestedNames = new HashSet();
    private GpsStatus currGpsStatus;
    private Button enableCoarseButton;
    private Button enableGPSButton;
    private final GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        /* class com.lavadip.skeye.config.AddLocationActivity.C00893 */

        public void onGpsStatusChanged(int event) {
            if (event == 4) {
                GpsStatus tempStatus = AddLocationActivity.this.locationManager.getGpsStatus(AddLocationActivity.this.currGpsStatus);
                if (AddLocationActivity.this.currGpsStatus == null) {
                    AddLocationActivity.this.currGpsStatus = tempStatus;
                }
                if (AddLocationActivity.this.currGpsStatus != null) {
                    int countVisible = 0;
                    int countFix = 0;
                    for (GpsSatellite sat : AddLocationActivity.this.currGpsStatus.getSatellites()) {
                        countVisible++;
                        if (sat.usedInFix()) {
                            countFix++;
                        }
                    }
                    if (AddLocationActivity.this.useGPSButton.getVisibility() == 0) {
                        AddLocationActivity.this.satStatusTxt.setText(Html.fromHtml(String.format("<small>Satellites visible: <b>%d</b>, in use: <b>%d</b></small>", Integer.valueOf(countVisible), Integer.valueOf(countFix))));
                        return;
                    }
                    AddLocationActivity.this.satStatusTxt.setText(Html.fromHtml(String.format("Satellites visible: <b>%d</b><br/>Satellites in use: <b>%d</b>", Integer.valueOf(countVisible), Integer.valueOf(countFix))));
                }
            }
        }
    };
    private Location gpsLocation;
    private TextView gpsStatusTxt = null;
    private final Set<String> gpsSuggestedNames = new HashSet();
    private boolean listeningToGPS = false;
    private EditText locNameEdit;
    private LocationManager locationManager = null;
    private EditText manualAltText;
    private final TextWatcher manualChangeWatcher = new TextWatcher() {
        /* class com.lavadip.skeye.config.AddLocationActivity.C00871 */

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            try {
                double latitude = Double.valueOf(AddLocationActivity.this.manualLatText.getText().toString()).doubleValue();
                double longitude = Double.valueOf(AddLocationActivity.this.manualLongText.getText().toString()).doubleValue();
                AddLocationActivity.this.worldMapView.setLatLong(latitude, longitude);
                AddLocationActivity.this.manualSuggestedNames.clear();
                new LocationNameFinder(latitude, longitude, AddLocationActivity.this, AddLocationActivity.this.manualSuggestedNames).start();
            } catch (NumberFormatException e) {
            }
        }
    };
    private EditText manualLatText;
    private EditText manualLongText;
    private final Set<String> manualSuggestedNames = new HashSet();
    private final Handler myHandler = new Handler() {
        /* class com.lavadip.skeye.config.AddLocationActivity.HandlerC00882 */
        private int currTabId = 0;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    this.currTabId = msg.arg1;
                    updateButton();
                    return;
                case 1:
                    updateButton();
                    return;
                default:
                    return;
            }
        }

        private void updateButton() {
            AddLocationActivity.this.suggestedNameAdapter.clear();
            if (this.currTabId == 0) {
                Set<String> combinedSuggestedNames = new HashSet<>();
                combinedSuggestedNames.addAll(AddLocationActivity.this.gpsSuggestedNames);
                combinedSuggestedNames.addAll(AddLocationActivity.this.coarseSuggestedNames);
                synchronized (AddLocationActivity.this.gpsSuggestedNames) {
                    for (String name : combinedSuggestedNames) {
                        AddLocationActivity.this.suggestedNameAdapter.add(name);
                    }
                }
            } else {
                synchronized (AddLocationActivity.this.manualSuggestedNames) {
                    for (String name2 : AddLocationActivity.this.manualSuggestedNames) {
                        AddLocationActivity.this.suggestedNameAdapter.add(name2);
                    }
                }
            }
            View v = AddLocationActivity.this.findViewById(C0031R.C0032id.locationSuggestedNames);
            if (AddLocationActivity.this.suggestedNameAdapter.size() > 0) {
                v.setVisibility(0);
            } else {
                v.setVisibility(8);
            }
        }
    };
    private Intent myIntent;
    private TextView satStatusTxt = null;
    private ArrayList<String> suggestedNameAdapter;
    private final String[] tabIds = {"Automatic", "Manual"};
    private Button useCoarseButton;
    private Button useGPSButton;
    private WorldMapView worldMapView;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0031R.layout.activity_addlocation);
        SkEye.setupActivity(this);
        this.myIntent = getIntent();
        this.manualLatText = (EditText) findViewById(C0031R.C0032id.manualLat);
        this.manualLongText = (EditText) findViewById(C0031R.C0032id.manualLong);
        this.manualAltText = (EditText) findViewById(C0031R.C0032id.manualAlt);
        this.locNameEdit = (EditText) findViewById(C0031R.C0032id.locationNameEdit);
        if (Build.MODEL.equalsIgnoreCase("Kindle Fire")) {
            this.manualLatText.setRawInputType(3);
            this.manualLongText.setRawInputType(3);
            this.manualAltText.setRawInputType(3);
        }
        this.worldMapView = (WorldMapView) findViewById(C0031R.C0032id.world_map_view);
        this.manualLatText.addTextChangedListener(this.manualChangeWatcher);
        this.manualLongText.addTextChangedListener(this.manualChangeWatcher);
        this.gpsStatusTxt = (TextView) findViewById(C0031R.C0032id.gpsStatus);
        this.satStatusTxt = (TextView) findViewById(C0031R.C0032id.satelliteStatus);
        this.coarseStatusTxt = (TextView) findViewById(C0031R.C0032id.coarseStatus);
        this.enableGPSButton = (Button) findViewById(C0031R.C0032id.enableGPSButton);
        this.enableCoarseButton = (Button) findViewById(C0031R.C0032id.enableCoarseLocationButton);
        this.useGPSButton = (Button) findViewById(C0031R.C0032id.useGPSButton);
        this.useCoarseButton = (Button) findViewById(C0031R.C0032id.useCoarseLocationButton);
        this.locationManager = (LocationManager) getSystemService("location");
        this.suggestedNameAdapter = new ArrayList<>();
        TabHost tabHost = (TabHost) findViewById(C0031R.C0032id.addLocationTabs);
        tabHost.setup();
        int[] tabContentId = {C0031R.C0032id.locationTabAutomatic, C0031R.C0032id.locationTabManual};
        String[] tabNames = {getString(C0031R.string.automatic), getString(C0031R.string.specify_manually)};
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            /* class com.lavadip.skeye.config.AddLocationActivity.C00904 */

            public void onTabChanged(String tabId) {
                int tabIndex = -1;
                int i = 0;
                while (true) {
                    if (i >= AddLocationActivity.this.tabIds.length) {
                        break;
                    } else if (AddLocationActivity.this.tabIds[i].equals(tabId)) {
                        tabIndex = i;
                        break;
                    } else {
                        i++;
                    }
                }
                AddLocationActivity.this.myHandler.obtainMessage(0, tabIndex, 0).sendToTarget();
            }
        });
        for (int i = 0; i < tabContentId.length; i++) {
            tabHost.addTab(tabHost.newTabSpec(this.tabIds[i]).setIndicator(makeIndicator(this, tabNames[i])).setContent(tabContentId[i]));
        }
        if (this.myIntent.getBooleanExtra("editRequested", false)) {
            ((TextView) findViewById(C0031R.C0032id.location_manage_title)).setText(getString(C0031R.string.editing_location));
            this.locNameEdit.setText(this.myIntent.getStringExtra("name"));
            this.manualLatText.setText(new StringBuilder().append(this.myIntent.getFloatExtra("latitude", 0.0f)).toString());
            this.manualLongText.setText(new StringBuilder().append(this.myIntent.getFloatExtra("longitude", 0.0f)).toString());
            this.manualAltText.setText(new StringBuilder().append(this.myIntent.getFloatExtra("altitude", 0.0f)).toString());
            switchToManual();
        }
        this.worldMapView.setOnTapListener(new WorldMapView.OnTapListener() {
            /* class com.lavadip.skeye.config.AddLocationActivity.C00915 */

            @Override // com.lavadip.skeye.config.WorldMapView.OnTapListener
            public void publishLatLong(float lat, float longitude) {
                AddLocationActivity.this.manualLatText.setText(new StringBuilder().append(lat).toString());
                AddLocationActivity.this.manualLongText.setText(new StringBuilder().append(longitude).toString());
                AddLocationActivity.this.switchToManual();
            }
        });
    }

    private static View makeIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(C0031R.layout.tabs_bg, (ViewGroup) null);
        ((TextView) view.findViewById(C0031R.C0032id.tabsText)).setText(title);
        return view;
    }

    /* access modifiers changed from: package-private */
    public void switchToManual() {
        ((TabHost) findViewById(C0031R.C0032id.addLocationTabs)).setCurrentTab(1);
    }

    public void clickUseManual(View v) {
        float altitude;
        if (checkName()) {
            try {
                float latitude = Float.valueOf(this.manualLatText.getText().toString()).floatValue();
                float longitude = Float.valueOf(this.manualLongText.getText().toString()).floatValue();
                if (this.manualAltText.getText().length() > 0) {
                    altitude = Float.valueOf(this.manualAltText.getText().toString()).floatValue();
                } else {
                    altitude = 0.0f;
                }
                if (checkLatLong(latitude, longitude)) {
                    wrapLocationAndFinish(this.locNameEdit.getText().toString(), latitude, longitude, altitude);
                }
            } catch (NumberFormatException e) {
                makeAlert(getString(C0031R.string.location_numeric_error));
            }
        }
    }

    private boolean checkLatLong(float latitude, float longitude) {
        if (latitude < -90.0f || latitude > 90.0f) {
            makeAlert(getString(C0031R.string.specify_correct_latitude));
            return false;
        } else if (longitude >= -180.0f && longitude <= 180.0f) {
            return true;
        } else {
            makeAlert(getString(C0031R.string.specify_correct_longitude));
            return false;
        }
    }

    public void clickUseGPS(View v) {
        if (checkName()) {
            wrapLocationAndFinish(this.locNameEdit.getText().toString(), (float) this.gpsLocation.getLatitude(), (float) this.gpsLocation.getLongitude(), (float) this.gpsLocation.getAltitude());
        }
    }

    public void clickUseCoarse(View v) {
        if (checkName()) {
            wrapLocationAndFinish(this.locNameEdit.getText().toString(), (float) this.coarseLocation.getLatitude(), (float) this.coarseLocation.getLongitude(), (float) this.coarseLocation.getAltitude());
        }
    }

    private void wrapLocationAndFinish(String name, float lat, float longitude, float altitude) {
        Intent data = new Intent(getIntent());
        data.putExtra("name", name);
        data.putExtra("latitude", lat);
        data.putExtra("longitude", longitude);
        data.putExtra("altitude", altitude);
        data.putExtra("location_id", this.myIntent.getIntExtra("location_id", -1));
        setResult(-1, data);
        finish();
    }

    private void makeAlert(String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage(msg).setCancelable(true).setNeutralButton("Ok", (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    private boolean checkName() {
        boolean ok = this.locNameEdit.getText().length() != 0;
        if (!ok) {
            this.locNameEdit.requestFocus();
            makeAlert(getString(C0031R.string.specify_location_name));
        }
        return ok;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        try {
            if (this.locationManager.isProviderEnabled("gps")) {
                startGPS();
            } else {
                handleGPSDisabled();
            }
        } catch (IllegalArgumentException e) {
            findViewById(C0031R.C0032id.add_location_gps_form).setVisibility(8);
        }
        if (this.locationManager.isProviderEnabled("network")) {
            startCoarse();
        } else {
            handleCoarseDisabled();
        }
        getWindow().setSoftInputMode(3);
    }

    private void startGPS() {
        this.enableGPSButton.setEnabled(false);
        this.enableGPSButton.setVisibility(8);
        this.gpsStatusTxt.setText(C0031R.string.waiting_for_gps_fix);
        this.gpsStatusTxt.setVisibility(0);
        this.satStatusTxt.setVisibility(0);
        this.locationManager.requestLocationUpdates("gps", 0, 0.0f, this);
        if (!this.listeningToGPS) {
            this.listeningToGPS = true;
            this.locationManager.addGpsStatusListener(this.gpsListener);
        }
    }

    private void startCoarse() {
        this.enableCoarseButton.setEnabled(false);
        this.enableCoarseButton.setVisibility(8);
        this.coarseStatusTxt.setText(C0031R.string.waiting_for_coarse_location);
        this.coarseStatusTxt.setVisibility(0);
        this.locationManager.requestLocationUpdates("network", 0, 0.0f, this);
    }

    public void onProviderEnabled(String provider) {
        if (provider.equals("gps")) {
            startGPS();
        }
    }

    private void handleGPSDisabled() {
        this.gpsLocation = null;
        this.gpsStatusTxt.setVisibility(8);
        this.satStatusTxt.setText("");
        this.enableGPSButton.setVisibility(0);
        this.useGPSButton.setVisibility(8);
    }

    private void handleCoarseDisabled() {
        this.coarseLocation = null;
        this.coarseStatusTxt.setVisibility(8);
        this.enableCoarseButton.setVisibility(0);
        this.useCoarseButton.setVisibility(8);
    }

    public void onProviderDisabled(String provider) {
        if (provider.equals("gps")) {
            handleGPSDisabled();
        } else if (provider.equals("network")) {
            handleCoarseDisabled();
        }
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Log.d("Location", "Provider Status changed:" + arg0 + "," + arg1 + "," + arg2);
    }

    public void showLocationOptions(View v) {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
    }

    public void clickSuggestedNames(View v) {
        new CustomDialog.Builder(this).setTitle(C0031R.string.title_suggested_names).setSingleChoiceItems((CharSequence[]) this.suggestedNameAdapter.toArray(new String[0]), 0, new DialogInterface.OnClickListener() {
            /* class com.lavadip.skeye.config.AddLocationActivity.DialogInterface$OnClickListenerC00926 */

            public void onClick(DialogInterface dialog, int which) {
                AddLocationActivity.this.locNameEdit.setText((CharSequence) AddLocationActivity.this.suggestedNameAdapter.get(which));
                dialog.dismiss();
            }
        }).create().show();
    }

    private final class LocationNameFinder extends Thread {
        private final Context context;
        private final double latitude;
        private final double longitude;
        private final Set<String> result;

        public LocationNameFinder(double latitude2, double longitude2, Context context2, Set<String> result2) {
            this.latitude = latitude2;
            this.longitude = longitude2;
            this.context = context2;
            this.result = result2;
        }

        private void addIfNotNull(String str) {
            if (str != null) {
                this.result.add(str);
            }
        }

        public void run() {
            try {
                List<Address> locations = new Geocoder(this.context).getFromLocation(this.latitude, this.longitude, 2);
                synchronized (this.result) {
                    for (Address addr : locations) {
                        addIfNotNull(addr.getFeatureName());
                        addIfNotNull(addr.getSubAdminArea());
                        addIfNotNull(addr.getLocality());
                    }
                }
                AddLocationActivity.this.myHandler.sendEmptyMessage(1);
            } catch (IOException e) {
                Log.i("SKEYE", "No network available");
            } catch (IllegalArgumentException e2) {
                Log.e("SKEYE", "Invalid lat or longitude specified");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.locationManager.removeUpdates(this);
        this.locationManager.removeGpsStatusListener(this.gpsListener);
        this.listeningToGPS = false;
    }

    public void onLocationChanged(Location loc) {
        String provider = loc.getProvider();
        if (provider.equals("gps")) {
            this.gpsLocation = loc;
            this.gpsStatusTxt.setText(Html.fromHtml(formatLocation(loc)));
            this.useGPSButton.setVisibility(0);
            new LocationNameFinder(this.gpsLocation.getLatitude(), this.gpsLocation.getLongitude(), this, this.gpsSuggestedNames).start();
        } else if (provider.equals("network")) {
            this.coarseLocation = loc;
            this.coarseStatusTxt.setText(Html.fromHtml(formatLocation(loc)));
            this.useCoarseButton.setVisibility(0);
            new LocationNameFinder(this.coarseLocation.getLatitude(), this.coarseLocation.getLongitude(), this, this.coarseSuggestedNames).start();
        }
    }

    private static String formatLocation(Location loc) {
        return String.format("<small>Lat: </small>%.2f°<small> Long: </small>%.2f°<small> Alt: </small>%.0f<small>m</small>", Double.valueOf(loc.getLatitude()), Double.valueOf(loc.getLongitude()), Double.valueOf(loc.getAltitude()));
    }
}
