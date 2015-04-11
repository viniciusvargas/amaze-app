package amaze.inspiring.mobi.amazeapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.util.List;

/**
 * Created by Vinicius on 30/03/2015.
 */
public class BeaconListViewAdapter extends BaseAdapter{

    private Context context;
    List<Beacon> beaconList;

    public BeaconListViewAdapter(Context context, List<Beacon> beaconList) {
        this.beaconList = beaconList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return beaconList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Beacon item = beaconList.get(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.activity_list_beacons_item, null);
        TextView beaconText = (TextView) v
                .findViewById(R.id.mBeaconId1);

        beaconText.setText(item.getId1().toString());

        return v;
    }

    @Override
    public long getItemId(int position) {
        return beaconList.indexOf(getItem(position));
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}
