package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Avaliacao_Produto implements Serializable {

    private int id_Avaliacao_Produto;
    private Produto fk_Produto;
    private PessoaFisica fk_Pessoa_Fisica;
    private String comentario;
    private int nota;

    public Avaliacao_Produto() {
    }

    public Avaliacao_Produto(Produto fk_Produto, PessoaFisica fk_Pessoa_Fisica, String comentario, int nota) {

        this.fk_Produto = fk_Produto;
        this.fk_Pessoa_Fisica = fk_Pessoa_Fisica;
        this.comentario = comentario;
        this.nota = nota;
    }

    public int getId_Avaliacao_Produto() {
        return id_Avaliacao_Produto;
    }

    public void setId_Avaliacao_Produto(int id_Avaliacao_Produto) {
        this.id_Avaliacao_Produto = id_Avaliacao_Produto;
    }

    public Produto getFk_Produto() {
        return fk_Produto;
    }

    public void setFk_Produto(Produto fk_Produto) {
        this.fk_Produto = fk_Produto;
    }

    public PessoaFisica getFk_Pessoa_Fisica() {
        return fk_Pessoa_Fisica;
    }

    public void setFk_Pessoa_Fisica(PessoaFisica fk_Pessoa_Fisica) {
        this.fk_Pessoa_Fisica = fk_Pessoa_Fisica;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }
}
