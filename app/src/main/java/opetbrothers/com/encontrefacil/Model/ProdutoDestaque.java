package opetbrothers.com.encontrefacil.Model;

import java.sql.Timestamp;


public class ProdutoDestaque {

	private int id_produto_destaque;
	private Produto fk_produto;
	private Timestamp data_entrada;
	private Timestamp data_saida;
	private int exposicao;

	public ProdutoDestaque() {
	}

	public ProdutoDestaque(Produto fk_produto, Timestamp data_entrada, Timestamp data_saida, int exposicao) {
		this.fk_produto = fk_produto;
		this.data_entrada = data_entrada;
		this.data_saida = data_saida;
		this.exposicao = exposicao;
	}

	public int getId_produto_destaque() {
		return id_produto_destaque;
	}
	public void setId_produto_destaque(int id_produto_destaque) {
		this.id_produto_destaque = id_produto_destaque;
	}
	public Produto getFk_produto() {
		return fk_produto;
	}
	public void setFk_produto(Produto fk_produto) {
		this.fk_produto = fk_produto;
	}
	public Timestamp getData_entrada() {
		return data_entrada;
	}
	public void setData_entrada(Timestamp data_entrada) {
		this.data_entrada = data_entrada;
	}
	public Timestamp getData_saida() {
		return data_saida;
	}
	public void setData_saida(Timestamp data_saida) {
		this.data_saida = data_saida;
	}
	public int getExposicao() {
		return exposicao;
	}
	public void setExposicao(int exposicao) {
		this.exposicao = exposicao;
	}
	
	
	
}
