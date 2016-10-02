package it.uniba.di.ivu.progettotesi.appturismo;


import android.os.Parcelable;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class AppClusterItem implements ClusterItem {

    private PaginaDiario paginaDiario;
    private final LatLng mPosition;
    private BitmapDescriptor markerDescriptor;
    private String url;

    public AppClusterItem(PaginaDiario paginaDiario){
        this.paginaDiario=paginaDiario;
        mPosition=new LatLng(paginaDiario.getLatitudine(),paginaDiario.getLongitudine());
        this.markerDescriptor=paginaDiario.getImmagineMappa();
        this.url=paginaDiario.getUrl();
    }
    /*
    public AppClusterItem(double latitude, double longitude,BitmapDescriptor bitmapDescriptor, String url) {
        mPosition = new LatLng(latitude, longitude);
        this.markerDescriptor=bitmapDescriptor;
        this.url=url;
    }*/
    public Pagina getPaginaDiario(){
        Pagina pagina=new Pagina(Double.toString(paginaDiario.getLatitudine()),Double.toString(paginaDiario.getLongitudine()),paginaDiario.getUrl(),paginaDiario.getDescrizione(),paginaDiario.getLuogo(),paginaDiario.getDataPagina(),paginaDiario.getOra(),paginaDiario.getDiario());
        return pagina;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getPosizione(){
        Double longitude=getPosition().longitude;
        Double latitudine=getPosition().latitude;
        String lon=longitude.toString();
        String lat=latitudine.toString();
        return lon+","+lat;
    }

    public BitmapDescriptor getIcon(){
        return markerDescriptor;
    }
    public String getUrl(){return url;}
}

