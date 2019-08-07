package semantico;

public class Id {
	public String nome = "";
	public String tipo = "";

	public Id(String nome1, String tipo1) {
		nome = nome1;
		tipo = tipo1;
	}

	public Id() {
		// TODO Auto-generated constructor stub
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
