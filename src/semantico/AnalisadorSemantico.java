package semantico;

import java.util.LinkedList;


// codigo baseado nas aulas e pseudocodigos dados pelo professor de compilares


public class AnalisadorSemantico {
	public static LinkedList<Id> pilhaId = new LinkedList<>();
	public static String tComandos = "";
	public static int e = 0;

	// como saber se o identificador é declarado ou usado
	// logica principal do codigo

	public static Boolean comparaVariavel(Id id) {
		// se o escopo foi aberto então eu não vou adicionar nada na pilha
		if (e > 0) {
			if (reconhecerIdentificador(id.getNome())) {
				return true;
			} else {
				return false;
			}
			// caso tenha sido aberto então adiciono na pilha
		} else {
			if (adicionaVariavelNaPilha(id)) {
				return true;
			} else {
				return false;
			}
		}

	}
	// exercicio de sala de aula para poder declarar uma funcao como variavel

	public static Boolean comparaProcedimento(Id id) {
		// se o escopo foi aberto então eu não vou adicionar nada na pilha
		if (e > 0) {
			// tá sendo usado
			if (reconhecerIdentificador(id.nome)) {
				return true;
			} else {
				return false;
			}
			// caso tenha sido aberto então adiciono na pilha
		} else {
			// ta sendo declarado
			if (adicionaProcedimentoNaPilha(id)) {
				return true;
			} else {
				return false;
			}
		}
	}

	// procura em toda pilha se existe o identificador
	public static Boolean reconhecerIdentificador(String id) {
		int j = 0;

		while (pilhaId.size() - 1 > j) {
			// System.out.println("valores da pilha"+pilhaId.get(j).nome);
			if (pilhaId.get(j).nome.equals(id)) {
				if (pilhaId.get(j).tipo.equals("procedure")) {
					return true;
				} else {
					return comparaTipo(pilhaId.get(j).tipo);
				}
			}
			j = j + 1;
		}

		System.out.println("problema semântico, " + "não foi encontrado Identificador, "
				+ "erro na funcao reconhecerIdentifcador");
		return false;
	}

	public static Boolean comparaTipo(String tipoId) {
		//System.out.println("Comandos: " + tComandos + " Tipos: " + tipoId);
		if (tComandos.equals("")) {
			tComandos = tipoId;
			return true;
		} else if (tComandos.equals("integer")) {
			if (tipoId.equals("integer")) {
				return true;
			} else if (tipoId.matches("\\*|\\/")) {
				return true;
			} else if (tipoId.matches("<=|>=|<>|=|<|>")) {
				tComandos = "boolean";
				return true;
			} else if (tipoId.matches("real")) {
				System.out.println("Não pode ser: " + tipoId);
				return false;
			}

		} else if (tComandos.equals("real")) {
			if (tipoId.equals("integer")) {
				return true;
			} else if (tipoId.matches("\\*|\\/")) {
				return true;
			} else if (tipoId.equals("real")) {
				return true;
			} else {
				System.out.println("erro semantico no real");
				return false;
			}

		} else if (tComandos.equals("boolean")) {
			if (tipoId.equals("boolean")) {
				return true;
			} else if (tipoId.matches("=|<=|>=|<>|and|or|")) {
				return true;
			} else if (tipoId.matches("\\w.*")) {

				return true;
			} else {
				System.out.println("erro semantico no boleano");
				return false;

			}

		}

		else {
			System.out.println("erro semantico com tipo De comando");
			return false;
		}
		System.out.println("erro semantico com tipo De comando");
		return false;

	}
     // exercicio de sala de aula
	public static Boolean adicionaProcedimentoNaPilha(Id id) {
		
		// verifica se é do tipo procedure
		if (id.tipo.equals("procedure")) {
			// verificar se já existe o procedimento na pilha
			if (procuraIdentificadores(id)) {
				System.out.println("Erro no procedimento já está na pilha");
				return false;
			}
			// System.out.println("valor do procedimento: "+pilhaId.get(pilhaId.size() -
			// 2).nome);
			// verificar se o nome do programa é igual ao nome do procedimento
			// ou seja, nome do procedimento que é add não pode ser teste
			if (id.nome.equals(pilhaId.get(pilhaId.size() - 2).nome)) {
				System.out.println("erro, procedimento tem o mesmo nome que program");
				return false;
			}
			pilhaId.push(id);
			pilhaId.push(new Id("$", "$"));
			return true;
		} else if (id.tipo.equals("program")) {
			pilhaId.push(new Id("$", "$"));
			pilhaId.push(id);
			return true;
		} else {
			System.out.println("erro no Adiciona procedimento na pilha: " + id.nome + " " + id.tipo);
			return false;
		}
	}
	public static void mostrarPilha() {

		System.out.println("\nResposta do Semantico, Identificador e Tipo da pilha: \n");
		for (Id a : pilhaId) {
			System.out.println("Nome: " + a.nome + " Tipo: " + a.tipo);
		}
	}

	public static Boolean adicionaVariavelNaPilha(Id id) {

		if (procuraIdentificadores(id)) {
			System.out.println("Erro semantico tentando adicionar variavel na pilha");
			return false;
		} else {
			pilhaId.push(id);
			return true;
		}
	}

	public static Boolean procuraIdentificadores(Id id) {

		int j = 0;
		while (pilhaId.get(j).nome != "$") {
			if (pilhaId.get(j).nome.equals(id.nome) && pilhaId.get(j).tipo.equals(id.tipo))
				return true;
			j = j + 1;
		}
		return false;
	}
	public static void comandosDesempilhar() {
		tComandos = "";
	}

	public static void tipoDasVariaveis(String tipo) {
		int j = 0;
		// System.out.println(pilhaId.size() );
		while ((j < (pilhaId.size() - 1)) && (pilhaId.get(j).tipo.equals(""))) {
			pilhaId.get(j++).tipo = tipo;
		}

	}

	public static void escopoAberto() {
		e++;

	}

	public static void escopoFechado() {
		e--;
		if (e == 0) {
			procedimentoDesempilhar();
		}
	}

	public static void procedimentoDesempilhar() {
		// Desempilha no começo do topo da pilha
		while (pilhaId.getFirst().nome != "$")
			pilhaId.pop();
		// Desempilha o "$"
		pilhaId.pop();

	}

	public static Boolean vazio() {
		// System.out.println("Aqui "+pilhaId.get(0).getNome()
		// +pilhaId.get(0).getTipo());
		// System.out.println("Aqui "+pilhaId.get(1).getNome()
		// +pilhaId.get(1).getTipo());
		if (pilhaId.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}




}