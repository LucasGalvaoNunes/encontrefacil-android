package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Categoria_Produto implements Serializable {
    private int id_Categoria_Produto;
    private String nome;

    public Categoria_Produto() {
    }

    public Categoria_Produto(String nome) {
        this.nome = nome;
    }

    public Categoria_Produto(int id_Categoria_Produto, String nome) {
        this.id_Categoria_Produto = id_Categoria_Produto;
        this.nome = nome;
    }

    public int getId_Categoria_Produto() {
        return id_Categoria_Produto;
    }

    public void setId_Categoria_Produto(int id_Categoria_Produto) {
        this.id_Categoria_Produto = id_Categoria_Produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
