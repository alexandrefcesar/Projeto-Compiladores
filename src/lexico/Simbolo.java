package lexico;

public class Simbolo {
	public String token;
	public String classificacao;
	public int linha;

	public Simbolo(String token1, String classificacao1, int linha1) {
		token = token1;
		classificacao = classificacao1;
		linha = linha1;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public int getLinha() {
		return linha;
	}

	public void setLinha(int linha) {
		this.linha = linha;
	}

	String formatar() {
		return String.format(" Token: %s| Classificao: %s| Linha: %s\n", token, classificacao, linha);
	}

}
