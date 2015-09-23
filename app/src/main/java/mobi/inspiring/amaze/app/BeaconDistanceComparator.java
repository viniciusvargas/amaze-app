package mobi.inspiring.amaze.app;

import org.altbeacon.beacon.Beacon;

import java.util.Comparator;

/**
 * Created by Vinicius on 22/06/2015.
 */
public class BeaconDistanceComparator implements Comparator<Beacon> {
    public int compare(Beacon beacon1, Beacon beacon2) {
        return new Double(beacon1.getDistance()).
                compareTo(beacon2.getDistance());
    }
}
