package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 27/05/2017.
 */

public class FavoritosPessoaFisica implements Serializable {

    private int id_Favoritos;
    private PessoaFisica fk_Pessoa_Fisica;
    private Produto fk_Produto;

    public FavoritosPessoaFisica() {
    }

    public FavoritosPessoaFisica(PessoaFisica fk_Pessoa_Fisica, Produto fk_Produto) {
        this.fk_Pessoa_Fisica = fk_Pessoa_Fisica;
        this.fk_Produto = fk_Produto;
    }

    public int getId_Favoritos() {
        return id_Favoritos;
    }

    public void setId_Favoritos(int id_Favoritos) {
        this.id_Favoritos = id_Favoritos;
    }

    public PessoaFisica getFk_Pessoa_Fisica() {
        return fk_Pessoa_Fisica;
    }

    public void setFk_Pessoa_Fisica(PessoaFisica fk_Pessoa_Fisica) {
        this.fk_Pessoa_Fisica = fk_Pessoa_Fisica;
    }

    public Produto getFk_Produto() {
        return fk_Produto;
    }

    public void setFk_Produto(Produto fk_Produto) {
        this.fk_Produto = fk_Produto;
    }
}
