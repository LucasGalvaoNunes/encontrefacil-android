package opetbrothers.com.encontrefacil.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Lucas Galvao Nunes on 11/06/2017.
 */

public class Route {
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
