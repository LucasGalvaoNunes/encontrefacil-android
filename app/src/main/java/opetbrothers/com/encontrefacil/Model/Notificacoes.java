package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 27/05/2017.
 */

public class Notificacoes implements Serializable {

    private int id_Notificacoes;
    private Produto fk_Produto;
    private PessoaJuridica fk_Pessoa_Juridica;
    private String detalhes;

    public Notificacoes() {
    }

    public Notificacoes(int id_Notificacoes, Produto fk_Produto, PessoaJuridica fk_Pessoa_Juridica, String detalhes) {
        this.id_Notificacoes = id_Notificacoes;
        this.fk_Produto = fk_Produto;
        this.fk_Pessoa_Juridica = fk_Pessoa_Juridica;
        this.detalhes = detalhes;
    }

    public int getId_Notificacoes() {
        return id_Notificacoes;
    }

    public void setId_Notificacoes(int id_Notificacoes) {
        this.id_Notificacoes = id_Notificacoes;
    }

    public Produto getFk_Produto() {
        return fk_Produto;
    }

    public void setFk_Produto(Produto fk_Produto) {
        this.fk_Produto = fk_Produto;
    }

    public PessoaJuridica getFk_Pessoa_Juridica() {
        return fk_Pessoa_Juridica;
    }

    public void setFk_Pessoa_Juridica(PessoaJuridica fk_Pessoa_Juridica) {
        this.fk_Pessoa_Juridica = fk_Pessoa_Juridica;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }
}
