package opetbrothers.com.encontrefacil.Model;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Localizacao {

    private int id_localizacao;
    private String longitude;
    private String latitude;

    public int getId_localizacao() {
        return id_localizacao;
    }

    public void setId_localizacao(int id_localizacao) {
        this.id_localizacao = id_localizacao;
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
