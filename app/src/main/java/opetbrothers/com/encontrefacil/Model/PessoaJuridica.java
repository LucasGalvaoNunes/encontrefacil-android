package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 27/05/2017.
 */

public class PessoaJuridica implements Serializable{


    private int id_PessoaJuridica;
    private Pessoa fk_Pessoa;
    private Categoria_Loja fk_Categoria_Loja;
    private Localizacao fk_Localizacao;
    private String senha;
    private String razao_Social;
    private String cnpj;
    private String codigo_verificacao;


    public PessoaJuridica() {
    }

    public PessoaJuridica(Pessoa fk_Pessoa, Categoria_Loja fk_Categoria_Loja, Localizacao fk_Localizacao, String senha, String razao_Social, String cnpj) {
        this.fk_Pessoa = fk_Pessoa;
        this.fk_Categoria_Loja = fk_Categoria_Loja;
        this.fk_Localizacao = fk_Localizacao;
        this.senha = senha;
        this.razao_Social = razao_Social;
        this.cnpj = cnpj;
    }

    public int getId_PessoaJuridica() {
        return id_PessoaJuridica;
    }

    public void setId_PessoaJuridica(int id_PessoaJuridica) {
        this.id_PessoaJuridica = id_PessoaJuridica;
    }

    public Pessoa getFk_Pessoa() {
        return fk_Pessoa;
    }

    public void setFk_Pessoa(Pessoa fk_Pessoa) {
        this.fk_Pessoa = fk_Pessoa;
    }

    public Categoria_Loja getFk_Categoria_Loja() {
        return fk_Categoria_Loja;
    }

    public void setFk_Categoria_Loja(Categoria_Loja fk_Categoria_Loja) {
        this.fk_Categoria_Loja = fk_Categoria_Loja;
    }

    public Localizacao getFk_Localizacao() {
        return fk_Localizacao;
    }

    public void setFk_Localizacao(Localizacao fk_Localizacao) {
        this.fk_Localizacao = fk_Localizacao;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRazao_Social() {
        return razao_Social;
    }

    public void setRazao_Social(String razao_Social) {
        this.razao_Social = razao_Social;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCodigo_verificacao() {
        return codigo_verificacao;
    }

    public void setCodigo_verificacao(String codigo_verificacao) {
        this.codigo_verificacao = codigo_verificacao;
    }
}
