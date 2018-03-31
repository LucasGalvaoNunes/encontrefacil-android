package opetbrothers.com.encontrefacil.Util;

import java.util.List;

import opetbrothers.com.encontrefacil.Model.Route;

/**
 * Created by Lucas Galvao Nunes on 11/06/2017.
 */

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
