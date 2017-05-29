package opetbrothers.com.encontrefacil.Model;

import java.io.Serializable;

/**
 * Created by Lucas Galvao Nunes on 27/05/2017.
 */

public class PessoaFisica implements Serializable
{

    private int id_PessoaFisica;
    private Pessoa fk_Pessoa;
    private String cpf;


    public PessoaFisica() {
    }

    public PessoaFisica(int id_PessoaFisica, Pessoa fk_Pessoa, String cpf) {
        this.id_PessoaFisica = id_PessoaFisica;
        this.fk_Pessoa = fk_Pessoa;
        this.cpf = cpf;
    }

    public int getId_PessoaFisica() {
        return id_PessoaFisica;
    }

    public void setId_PessoaFisica(int id_PessoaFisica) {
        this.id_PessoaFisica = id_PessoaFisica;
    }

    public Pessoa getFk_Pessoa() {
        return fk_Pessoa;
    }

    public void setFk_Pessoa(Pessoa fk_Pessoa) {
        this.fk_Pessoa = fk_Pessoa;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}
