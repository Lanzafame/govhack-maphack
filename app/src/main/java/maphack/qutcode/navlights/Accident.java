package maphack.qutcode.navlights;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.heatmaps.WeightedLatLng;

/**
 * Created by Harrison on 4/07/2015.
 */
public class Accident extends WeightedLatLng{
    private LatLng loc;
    private double fatality;

    public Accident(LatLng location, double severity)
    {
        super(location, severity);
        loc = location;
        fatality = severity;
    }

    public LatLng getLocation() {
        return loc;
    }

    public double getFatality() {
        return fatality;
    }
}
