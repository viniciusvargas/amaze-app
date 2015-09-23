package mobi.inspiring.amaze.app;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.internal.Validate;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NearbyBeaconsFragment extends ListFragment implements BeaconConsumer {

    private BeaconManager beaconManager;
    private static boolean activityVisible;
    private ListView listView;
    public static List<Beacon> listBeacons = new ArrayList<>();
    List<String> beaconStringList = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        startBeaconSearch();
    }

    private void startBeaconSearch() {
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
//        beaconManager.setForegroundBetweenScanPeriod(10000);
        beaconManager.setForegroundScanPeriod(5000);
        beaconManager.bind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_nearby_beacons, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, new String[] {});
        setListAdapter(adapter);

        listView = getListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                 Toast.makeText(getActivity(), "Click on: " + position, Toast.LENGTH_SHORT).show();

                 Intent intent = new Intent(getActivity(), BeaconDetails.class);
                 intent.putExtra("beacon", listBeacons.get(position));
                 getActivity().startActivity(intent);
            }
        });
        startBeaconSearch();
    }

    @Override
    public void onBeaconServiceConnect() {
        MyRangeNotifier myRangeNotifier = new MyRangeNotifier();
        beaconManager.setRangeNotifier(myRangeNotifier);
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    class MyRangeNotifier implements RangeNotifier {
        Beacon closestBeacon = null;

        public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
            if (HomeActivity.isActivityVisible()) {
                if (beacons.size() > 0) {
                    beaconStringList.clear();
                    listBeacons.clear();

                    for (Beacon beacon : beacons) {
                        listBeacons.add(beacon);
                        beaconStringList.add(beacon.getId2().toHexString());
                    }

                    final String[] beaconStringArray = beaconStringList.toArray(new String[beaconStringList.size()]);
                    if (!beaconStringList.isEmpty()) {
                        getActivity().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, beaconStringArray);
                                        setListAdapter(adapter);
                                    }
                                }
                        );
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "No beacons found!", Toast.LENGTH_SHORT);


                }
            }

        }
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

}
