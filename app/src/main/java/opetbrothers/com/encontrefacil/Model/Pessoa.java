package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 27/05/2017.
 */

public class Pessoa implements Serializable {

    private int id_Pessoa;
    private String nome;
    private String sobrenome;
    private String telefone;
    private String foto;
    private String email;

    public Pessoa() {
    }

    public Pessoa(String nome, String sobrenome, String telefone, String foto, String email) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.telefone = telefone;
        this.foto = foto;
        this.email = email;
    }

    public int getId_Pessoa() {
        return id_Pessoa;
    }

    public void setId_Pessoa(int id_Pessoa) {
        this.id_Pessoa = id_Pessoa;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
