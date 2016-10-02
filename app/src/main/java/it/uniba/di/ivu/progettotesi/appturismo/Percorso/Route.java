package it.uniba.di.ivu.progettotesi.appturismo.Percorso;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;

    /////se voglio un waypoint
    //public String endAddress2;
    //public LatLng endLocation2;
    ////

    public String startAddress;
    public LatLng startLocation;
    public List<LatLng> points;
}
