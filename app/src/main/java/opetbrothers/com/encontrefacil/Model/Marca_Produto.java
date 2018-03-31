package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Marca_Produto implements Serializable {

    private int id_Marca_Produto;
    private String nome;

    public Marca_Produto() {
    }

    public Marca_Produto(String nome) {
        this.nome = nome;
    }

    public int getId_Marca_Produto() {
        return id_Marca_Produto;
    }

    public void setId_Marca_Produto(int id_Marca_Produto) {
        this.id_Marca_Produto = id_Marca_Produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
