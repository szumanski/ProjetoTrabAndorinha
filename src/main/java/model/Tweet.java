package model;

import java.sql.Date;

public class Tweet {
	
	
	private int id;
	private String conteudo;
	private Date data_postagem;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public Date getData_postagem() {
		return data_postagem;
	}
	public void setData_postagem(Date data_postagem) {
		this.data_postagem = data_postagem;
	}
	
	

}
