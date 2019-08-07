package lexico;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class AnalisadorExpressao {
    // o codigo foi feito usando expressões regulares baseados no site do dev 
	public String palavraReservada = "program|var|" + "integer|real|boolean|procedure|begin"
			+ "|end|if|then|else|while|do|not" + "|case|true|false";
	public String palavra = "[a-z]*|_*|[A-Z]*";
	public String identificador = "\\_*\\w+[\\_\\w+]*";
	public String delimitadores = "\\;|\\.|\\:|\\,|\\(|\\)";
	public String operadoresRelacionais = "=|<|>|<=|>=|<>";
	public String operadoresAditivos = "\\+|\\-";
	public String operadorAditivoOr = "or";
	public String operadorMultiplicativo = "\\*|/";
	public String operadorMultiplicativoAnd = "and";
	public String atribuicao = "\\:=";
	public String numerosInteiros = "\\d+";
	public String numerosReais = "\\d+\\.{1}\\d*";
	// public String comen= "\\{{1}[\\w\\W]*\\}{1}";
	public String comentario = ".*\\w+\\}{1}";
	public String potencia = "\\d+\\.{1}\\d*\\e[+|-]\\d*";
	public String espacoTabulacao = " |\t";
	static ArrayList tabela2 = new ArrayList();
	
	// funcao de retorno da tabela 
	public static ArrayList retorno() {
		ArrayList lista = tabela2;
		return lista;
	}
	
    // a funcao analisar vai receber uma lista de simbolos e o codigo pascal que vai 
	// ser analisado e gerado os simbolos
	public ArrayList analisar(List<Simbolo> tabela, List codigo) {

		int numeroLinhas = codigo.size();
		for (int l = 0; l < numeroLinhas; l++) { // esse for é para o numero de linhas

			String linha = codigo.get(l).toString();
			// mostrar codigo

			int tamanhoLinha = linha.length();
			for (int i = 0; i < tamanhoLinha;) {  // esse for é para o tamanho da linha
				// para não capturar espaços e tab
				
				//funcao substring quebra a string da linha, 
				//matches é usado para achar a expressão regular 
				// logica baseada no site do dev
				if (i + 1 <= tamanhoLinha && linha.substring(i, i + 1).matches(espacoTabulacao)) {
					i++;
				}
				
				else if (i + 1 <= tamanhoLinha && ("{".equals(linha.substring(i, i + 1)))) {
					int j = i + 1;

					while (j + 1 < tamanhoLinha) {
						j++;
						if ("}".equals(linha.substring(j, j + 1))) {
							break;
						}

					}

					if (j <= tamanhoLinha && (linha.substring(i, j + 1).matches(comentario))) {
						tabela.add(new Simbolo(linha.substring(i, j + 1), "Comentario", l + 1));

						i = j + 1;
					} else {
						JOptionPane.showMessageDialog(null, "Comentario Errado");
						System.exit(0);
					}
				}
				/*
				 * else if (i + 1 <= tamanhoLinha && ("{".equals(linha.substring(i, i + 1)))) {
				 * int j = i + 1; boolean comentarioAberto = true; String comentario = ""; //
				 * percorre para achar um fecha comentÃ¡rio atÃ© o final da linha while
				 * (comentarioAberto) { while (j <= tamanhoLinha) { if
				 * ("}".equals(linha.substring(j - 1, j))) { comentarioAberto = false; j++;
				 * break; } j++; } comentario += linha.substring(i, j - 1); // se o comentario
				 * ainda esta // aberto nas outras linhas entÃ£o continua . if
				 * (comentarioAberto) { if (l < numeroLinhas) { l++; linha =
				 * codigo.get(l).toString(); tamanhoLinha = linha.length(); i = 0; j = i + 1; }
				 * // se nao tem mais linhas a serem lidas entao hÃ¡ um erro. comentario nunca
				 * foi // fechado. else {
				 * System.out.println("erro no comentario: ComentÃ¡rio aberto e nunca fechado."
				 * ); System.exit(0); } System.out.println("Encontrada na linha" + (l + 1) +
				 * " ===>  " + linha); } } tabela.add(new Simbolo(comentario, "COMENTARIO", l +
				 * 1)); i = j - 1; }
				 */

				// se for uma palavra
				else if (i + 1 <= tamanhoLinha && linha.substring(i, i + 1).matches(palavra)) {
					// continua a iteraÃ§Ã£o para ver se Ã© palavra chave ou operadores
					int j = i + 1;
					// verificar se Ã© letra ou digito
					while (j < tamanhoLinha
							&& (Character.isLetterOrDigit(linha.charAt(j)) || linha.substring(j, j + 1).matches("_"))) {
						j++;
					}
					// aqui é para palavras reservadas
					if (j <= tamanhoLinha && linha.substring(i, j).matches(palavraReservada)) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, j), "Palavra Reservada", l + 1));

					}

					// aqui é o operador or 
					else if (j <= tamanhoLinha && linha.substring(i, j).matches(operadorAditivoOr)) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, j), "Operador Aditivo", l + 1));
					}
					// aqui é o operador and
					else if (j <= tamanhoLinha && linha.substring(i, j).matches(operadorMultiplicativoAnd)) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, j), "Operador Multiplicativo", l + 1));
					}
					// aqui o identificador
					else if (j <= tamanhoLinha && (linha.substring(i, j).matches(identificador)
							&& !(linha.substring(i, j).matches(operadorMultiplicativoAnd)
									&& linha.substring(i, j).matches(identificador)))) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, j), "Identificador", l + 1));
					}
					i = j;
				}
				// NUMERO inteiro ou real
				else if (i < tamanhoLinha && Character.isDigit(linha.charAt(i))) {
					int j = i + 1;
					while (j <= tamanhoLinha && (linha.substring(i, j).matches(numerosInteiros)
							|| linha.substring(i, j).matches(numerosReais))) {
						j++;
					}
					if (linha.substring(i, j - 1).matches(numerosInteiros)) {
						tabela.add(new Simbolo(linha.substring(i, j - 1), "Numero Inteiro", l + 1));
						i = j - 1;
					}
					// numero real
					else if (linha.substring(i, j - 1).matches(numerosReais)) {

						// primeiro testa se depois do real tem um E
						if (j < tamanhoLinha && linha.substring(j - 1, j).equals("e")) {

							// System.out.println(linha.substring(j - 1, j));
							j++;
							// depois testa se tem + ou -
							if (j < tamanhoLinha && (linha.substring(j - 1, j).matches("[+|-]"))) {

								j++;
								// para ficar pegando varios nÃºmeros
								while (j < tamanhoLinha) {
									if (linha.substring(j - 1, j).matches("\\d"))
										j++;
									else
										// quando não tiver mais numeros, então pode sair 
										break;

								}
								// depois testa a expressão
								if (linha.substring(i, j).matches(potencia)) {
									tabela.add(new Simbolo(linha.substring(i, j), "Potencia", l + 1));
									i = j;
									//System.out.println("aqui");
								}
							}

						} else {
							//System.out.println("else");
							tabela.add(new Simbolo(linha.substring(i, j - 1), "Numero Real", l + 1));
							i = j - 1;
						}

					}

				}
			   // para operadores aditivos 
				else if (i + 1 <= tamanhoLinha && linha.substring(i, i + 1).matches(operadoresAditivos)) {
					//criando nova instancia dos simbolos
					tabela.add(new Simbolo(linha.substring(i, i + 1), "Operador Aditivo", l + 1));
					i++;
				}
			    // para operadores relacionais
				else if (i + 1 <= tamanhoLinha && linha.substring(i, i + 1).matches(operadoresRelacionais)) {
					if (i + 2 <= tamanhoLinha && linha.substring(i, i + 2).matches(operadoresRelacionais)) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, i + 2), "Operador Relacional", l + 1));
						i += 2;
					} else {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, i + 1), "Operador Relacional", l + 1));
						i++;
					}
				}
			    // para delimitadores
				else if (i + 1 <= tamanhoLinha && linha.substring(i, i + 1).matches(delimitadores)) {
					if (i + 2 <= tamanhoLinha && (linha.substring(i, i + 2).matches(atribuicao))) {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, i + 2), "Atribuicao", l + 1));
						i = i + 2;
					} else {
						//criando nova instancia dos simbolos
						tabela.add(new Simbolo(linha.substring(i, i + 1), "Delimitador", l + 1));
						i++;
					}
				}
				// para operadores multiplicativos 
				else if (i + 1 <= tamanhoLinha && (linha.substring(i, i + 1).matches(operadorMultiplicativo))) {
					//criando nova instancia dos simbolos
					tabela.add(new Simbolo(linha.substring(i, i + 1), "Operador Multiplicativo", l + 1));
					i++;
				}
				// para erros
				else {
					JOptionPane.showMessageDialog(null, "Erro na linha:  " + l + "(" + linha.substring(i, i + 1) + ")");
					i++;
					System.exit(0);
				}

			} // fim do for do for de tamanho da linha

		} // fim do for de numero de linhas

		String teste = "";
		
		// for para exibir a tabela
		for (Simbolo k : tabela) {
			teste += k.formatar();
			tabela2.add(k.formatar());

		}
		System.out.println("Tabela de simblos do Lexico: \n");
		System.out.println(tabela2);
		return tabela2;

	}



}