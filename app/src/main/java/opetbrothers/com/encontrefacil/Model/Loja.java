package opetbrothers.com.encontrefacil.Model;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Loja {
    private int id_loja;
    private Localizacao fk_localizacao;
    private Usuario fk_usuario;
    private Categoria_Loja fk_categoria_loja;
    private String nome;
    private byte[] foto;

    public int getId_loja() {
        return id_loja;
    }

    public void setId_loja(int id_loja) {
        this.id_loja = id_loja;
    }

    public Localizacao getFk_localizacao() {
        return fk_localizacao;
    }

    public void setFk_localizacao(Localizacao fk_localizacao) {
        this.fk_localizacao = fk_localizacao;
    }

    public Usuario getFk_usuario() {
        return fk_usuario;
    }

    public void setFk_usuario(Usuario fk_usuario) {
        this.fk_usuario = fk_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria_Loja getFk_categoria_loja() {
        return fk_categoria_loja;
    }

    public void setFk_categoria_loja(Categoria_Loja fk_categoria_loja) {
        this.fk_categoria_loja = fk_categoria_loja;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
