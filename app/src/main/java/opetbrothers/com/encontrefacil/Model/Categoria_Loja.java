package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Categoria_Loja implements Serializable {

    private int id_Categoria_Loja;
    private String nome;

    public Categoria_Loja() {
    }

    public Categoria_Loja(int id_Categoria_Loja, String nome) {
        this.id_Categoria_Loja = id_Categoria_Loja;
        this.nome = nome;
    }

    public int getId_Categoria_Loja() {
        return id_Categoria_Loja;
    }

    public void setId_Categoria_Loja(int id_Categoria_Loja) {
        id_Categoria_Loja = id_Categoria_Loja;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
