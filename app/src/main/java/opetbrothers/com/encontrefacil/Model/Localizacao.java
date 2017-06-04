package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Localizacao implements Serializable {

    private int id_Localizacao;
    private String longitude;
    private String latitude;
    private String cidade;
    private String estado;
    private String bairro;
    public Localizacao() {
    }

    public Localizacao(String longitude, String latitude, String cidade, String estado, String bairro) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.cidade = cidade;
        this.estado = estado;
        this.bairro = bairro;
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



    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
}
