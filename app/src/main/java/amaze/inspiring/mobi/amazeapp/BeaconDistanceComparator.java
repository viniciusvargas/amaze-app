package amaze.inspiring.mobi.amazeapp;

import org.altbeacon.beacon.Beacon;

import java.util.Comparator;

/**
 * Created by Vinicius on 25/03/2015.
 */
public class BeaconDistanceComparator implements Comparator<Beacon> {

    @Override
    public int compare(final Beacon o1, final Beacon o2) {
        Double distance1 = o1.getDistance();
        Double distance2 = o2.getDistance();
        return distance1.compareTo(distance2);
    }
}
