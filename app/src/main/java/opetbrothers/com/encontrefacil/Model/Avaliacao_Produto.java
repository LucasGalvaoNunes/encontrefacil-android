package opetbrothers.com.encontrefacil.Model;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Avaliacao_Produto {

    private int id_avaliacao_produto;
    private Produto fk_produto;
    private Usuario fk_usuario;
    private String comentario;

    private int nota;


    public int getId_avaliacao_produto() {
        return id_avaliacao_produto;
    }
    public void setId_avaliacao_produto(int id_avaliacao_produto) {
        this.id_avaliacao_produto = id_avaliacao_produto;
    }
    public Produto getFk_produto() {
        return fk_produto;
    }
    public void setFk_produto(Produto fk_produto) {
        this.fk_produto = fk_produto;
    }
    public Usuario getFk_usuario() {
        return fk_usuario;
    }
    public void setFk_usuario(Usuario fk_usuario) {
        this.fk_usuario = fk_usuario;
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
