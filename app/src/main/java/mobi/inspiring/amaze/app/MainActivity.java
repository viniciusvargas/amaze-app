package mobi.inspiring.amaze.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;


public class MainActivity extends Activity implements BeaconConsumer{

    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;
    private static boolean activityVisible;

    private void startBeaconSearch() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.setForegroundBetweenScanPeriod(2000);
        beaconManager.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        startBeaconSearch();
        activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityPaused();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        MyRangeNotifier myRangeNotifier = new MyRangeNotifier();
        beaconManager.setRangeNotifier(myRangeNotifier);
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    class MyRangeNotifier implements RangeNotifier{
        final String TAG = "RangingActivity";
        Beacon closestBeacon = null;

        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (isActivityVisible()) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                    for (Beacon beacon : beacons) {
                        if (closestBeacon != null) {
                            if (beacon.getDistance() < closestBeacon.getDistance()) {
                                closestBeacon = beacon;
                            }
                        } else {
                            closestBeacon = beacon;
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, BeaconDetails.class);
                    intent.putExtra("beacon", closestBeacon);
                    MainActivity.this.beaconManager.unbind(MainActivity.this);
                    MainActivity.this.startActivity(intent);
                    finish();
                }
            }

        }
    }
}

