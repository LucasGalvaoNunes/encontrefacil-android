package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Produto implements Serializable{

    private int id_Produto;
    private Categoria_Produto fk_Categoria_Produto;
    private Marca_Produto fk_Marca_Produto;
    private PessoaJuridica fk_Pessoa_Juridica;
    private String nome;
    private String descricao;
    private String preco;
    private String foto;
    private Timestamp data_Publicacao;
    private int is_active;

    public Produto(Categoria_Produto fk_Categoria_Produto, Marca_Produto fk_Marca_Produto,PessoaJuridica fk_Pessoa_Juridica, String nome, String descricao, String preco, String foto, Timestamp data_Publicacao, int is_active) {
        this.fk_Categoria_Produto = fk_Categoria_Produto;
        this.fk_Marca_Produto = fk_Marca_Produto;
        this.fk_Pessoa_Juridica = fk_Pessoa_Juridica;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.foto = foto;
        this.data_Publicacao = data_Publicacao;
        this.is_active = is_active;
    }

    public Produto() {
    }

    public Produto(Categoria_Produto fk_Categoria_Produto, Marca_Produto fk_Marca_Produto,PessoaJuridica fk_Pessoa_Juridica, String nome, String descricao, String preco, String foto, Timestamp data_Publicacao) {
        this.fk_Categoria_Produto = fk_Categoria_Produto;
        this.fk_Marca_Produto = fk_Marca_Produto;
        this.fk_Pessoa_Juridica = fk_Pessoa_Juridica;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.foto = foto;
        this.data_Publicacao = data_Publicacao;
    }


    public int getId_Produto() {
        return id_Produto;
    }

    public void setId_Produto(int id_Produto) {
        this.id_Produto = id_Produto;
    }

    public Categoria_Produto getFk_Categoria_Produto() {
        return fk_Categoria_Produto;
    }

    public void setFk_Categoria_Produto(Categoria_Produto fk_Categoria_Produto) {
        this.fk_Categoria_Produto = fk_Categoria_Produto;
    }

    public Marca_Produto getFk_Marca_Produto() {
        return fk_Marca_Produto;
    }

    public void setFk_Marca_Produto(Marca_Produto fk_Marca_Produto) {
        this.fk_Marca_Produto = fk_Marca_Produto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Timestamp getData_Publicacao() {
        return data_Publicacao;
    }

    public void setData_Publicacao(Timestamp data_Publicacao) {
        this.data_Publicacao = data_Publicacao;
    }

    public PessoaJuridica getFk_Pessoa_Juridica() {
        return fk_Pessoa_Juridica;
    }

    public void setFk_Pessoa_Juridica(PessoaJuridica fk_Pessoa_Juridica) {
        this.fk_Pessoa_Juridica = fk_Pessoa_Juridica;
    }
    
    public int getIs_active() {
		return is_active;
	}

	public void setIs_active(int is_active) {
		this.is_active = is_active;
	}
}
