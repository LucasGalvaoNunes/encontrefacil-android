package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Localizacao implements Serializable {

    private int id_Localizacao;
    private String longitude;
    private String latitude;

    public Localizacao() {
    }

    public Localizacao(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getId_Localizacao() {
        return id_Localizacao;
    }

    public void setId_Localizacao(int id_Localizacao) {
        this.id_Localizacao = id_Localizacao;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
