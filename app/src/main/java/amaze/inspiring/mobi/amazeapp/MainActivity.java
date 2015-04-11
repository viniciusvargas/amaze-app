package amaze.inspiring.mobi.amazeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Collections;


public class MainActivity extends Activity implements BeaconConsumer{

    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
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
                MainActivity.this.beaconManager.setRangeNotifier(null);
                MainActivity.this.startActivity(intent);

            }
        }
    }
}

