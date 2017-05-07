package opetbrothers.com.encontrefacil.Model;

/**
 * Created by Lucas Galvao Nunes on 23/04/2017.
 */

public class Usuario {
    private int id_usuario;
    private Localizacao fk_localizacao;
    private String nome;
    private String sobrenome;
    private String email;
    private String senha;

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Localizacao getFk_localizacao() {
        return fk_localizacao;
    }

    public void setFk_localizacao(Localizacao fk_localizacao) {
        this.fk_localizacao = fk_localizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
