package it.uniba.di.ivu.progettotesi.appturismo.Percorso;
import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
