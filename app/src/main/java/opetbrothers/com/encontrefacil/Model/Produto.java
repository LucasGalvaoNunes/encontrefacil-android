package opetbrothers.com.encontrefacil.Model;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Produto {
    private int id_produto;
    private Marca_Produto fk_marca_produto;
    private Categoria_Produto fk_categoria_produto;
    private Loja fk_loja;
    private String nome;
    private String descricao;
    private String preco;
    private byte[] foto;

    public int getId_produto() {
        return id_produto;
    }

    public void setId_produto(int id_produto) {
        this.id_produto = id_produto;
    }

    public Marca_Produto getFk_marca_produto() {
        return fk_marca_produto;
    }

    public void setFk_marca_produto(Marca_Produto fk_marca_produto) {
        this.fk_marca_produto = fk_marca_produto;
    }

    public Categoria_Produto getFk_categoria_produto() {
        return fk_categoria_produto;
    }

    public void setFk_categoria_produto(Categoria_Produto fk_categoria_produto) {
        this.fk_categoria_produto = fk_categoria_produto;
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

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Loja getFk_loja() {
        return fk_loja;
    }

    public void setFk_loja(Loja fk_loja) {
        this.fk_loja = fk_loja;
    }
}
