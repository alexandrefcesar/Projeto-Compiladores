package sintatico;

import java.util.LinkedList;

import java.util.List;

import lexico.Simbolo;
import semantico.AnalisadorSemantico;
import semantico.Id;


// codigo baseado na gramatica dada pelo professor de compiladores

public class AnalisadorSintatico {
	LinkedList<Simbolo> tabelas = new LinkedList<>();
	LinkedList<String> identificadores = new LinkedList<>();
	private int indice = 0;
	private static Id ide;

	
	
	// foi retirado os comentários do código, os comentários não são analisados nesta parte
	public void retirarComentario(LinkedList<Simbolo> tabela) {
		int i = 0;
		for (Simbolo s : tabela) {
			if (s.getClassificacao() != "Comentario") {
				i++;
				tabelas.add(s);
			}
		}
	}
    // funcao proximo chama o prox token da lista de simbolos 
	public void proximo() {
		if (indice < tabelas.size() - 1) {
			indice++;
		} else {
			System.out.println("fim do programa");
			System.exit(0);
		}

	}
    //funcao analisador sintatico que executa program e é baseado na aula
	public void analisadorSintatico(LinkedList<Simbolo> tabela, List codigo) {
		retirarComentario(tabela);
		ide = new Id();
		identificadores = new LinkedList();
		program();
	}


	 // função program que foi passada como pseudo codigo pelo professor
	public void program() {

		if (tabelas.get(indice).getToken().matches("program")) {
			proximo();

		    // usado para guarda os nomes e tipos e verificar se o identificador de 
			// program tem o mesmo nome que funcoes
		    ide.nome = tabelas.get(indice).getToken();
		    ide.tipo = tabelas.get(indice-1).getToken();
			
			if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
				proximo();
				//tá testando tbm se program tem o mesmo nome 
			    AnalisadorSemantico.comparaProcedimento(ide);
				
				if (tabelas.get(indice).getToken().matches(";")) {
					proximo();
					declaracaoVariaveis();
					declaracaoFuncoes();
					comandoComposto();

					if (tabelas.get(indice).getToken().equals(".")) {
						System.out.println("\nSintatico: Programa concluido com sucesso");
					} else {
						System.out.printf("Erro no ponto Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
								tabelas.get(indice).getToken());
					}
				} else {
					System.out.printf("Erro no delimitador Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
				}
			} else {
				System.out.printf("Erro no Identificador Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
			}
		} else {
			System.out.printf("Erro no program Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
					tabelas.get(indice).getToken());
		}
	}


	 // declarações_variáveis → 
	//var lista_declarações_variáveis 
	//| ε
	

	public Boolean declaracaoVariaveis() {

		if (tabelas.get(indice).getToken().matches("var")) {
			proximo();
			return lVariaveis();
		}
		 //ε
		// continua se não tem essa lista
		return true;

	}

	// lista_declarações_variáveis →
	//lista_declarações_variáveis
	// lista_de_identificadores: REGEX_tipo; 
	//| lista_de_identificadores: REGEX_tipo;

	public Boolean lVariaveis() {
		// pode existir pelo menos uma variavel nessa lista.
		// vai lá para funcao de baixo
		if (listaId()) {
			proximo();
			if (tabelas.get(indice).getToken().matches("integer|INTEGER|real|REAL|" + "boolean|BOOLEAN|char|CHAR")) {
				//definir os tipos de variáveis que foram declaradas
				//System.out.println(tabelas.get(indice).token);
				//para cadastrar os tipos 
				AnalisadorSemantico.tipoDasVariaveis(tabelas.get(indice).token);
				proximo();
				if (tabelas.get(indice).getToken().equals(";")) {
					proximo();
					return lVariaveis();
				} else {
					System.out.printf("Erro no delimitador Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
				}

			} else {
				System.out.printf("Erro no tipo Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
			}
		} else if(!AnalisadorSemantico.vazio()) {
			
			if (tabelas.get(indice).getToken().matches("begin") || tabelas.get(indice).getToken().matches("procedure")) { 
			return true;
			}
			else {
				System.out.println("erro ");
			}
		}

		System.out.println("erro");
		return false;
		
	}

	
	 // lista_de_identificadores → 
	 // id | lista_de_identificadores, id
	 

	public Boolean listaId() {
		// se for IDENTIFICADOR entao deve entrar
		if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
			ide = new Id();
			ide.nome = tabelas.get(indice).token;
			// verificar se o identificador foi declarada ou usado
		 if(AnalisadorSemantico.comparaVariavel(ide)) {
			proximo();
			if (tabelas.get(indice).getToken().matches(",")) {
				proximo();
				return listaId();
			} else if (tabelas.get(indice).getToken().matches(":")) {
				return true;
			} else {
				System.out.printf("Erro no delimitador Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
			}
		 }
		else {
            System.out.println("erro no semantico");
			return false;
		}
		 /* se já declarou as variáveis, pode ser um REGEX_begin|REGEX_procedure */
	}
		else return false;
	}
	// ----------------------------variaveis termina aqui
	// -----------------------------

	// ----------------------------funcoes começa aqui

	 // declarações_de_subprogramas → 
	//declarações_de_subprogramas
	 // declaração_de_subprograma; 
	//| ε
	 

	public Boolean declaracaoFuncoes() {

		// pode existir varias funcoes
		if (funcao()) {
			if (tabelas.get(indice).getToken().equals(";")) {
				proximo();
				return declaracaoFuncoes();
			}
		} else if (tabelas.get(indice).getToken().matches("begin")) {
			return true;
		}
		return true;
	}
	  
         // declaração_de_subprograma →
        //  procedure id argumentos;
        //  declarações_variáveis
        //  declarações_de_subprogramas
        //  comando_composto
     

	public Boolean funcao() {
		
		if (procedimentoId()) {
			proximo();
			if (declaracaoVariaveis()) {
				if (declaracaoFuncoes()) {
					if (comandoComposto()) {
						return true;
					} else {
						System.out.printf("Erro no Comando Composto Linha: %d - Token: %s \n",
								tabelas.get(indice).getLinha(), tabelas.get(indice).getToken());
						return false;
					}
				} else {
					System.out.printf("Erro nas funcoes Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
				}

			} else {
				System.out.printf("Erro na declaracao das variaveis Linha: %d - Token: %s \n",
						tabelas.get(indice).getLinha(), tabelas.get(indice).getToken());
				return false;
			}

		}

		return false;
	}
// atividade de semantico
	//procedimento id
	public Boolean procedimentoId() {
		// formato correto
		if (tabelas.get(indice).getToken().matches("procedure")) {
			proximo();
			ide = new Id();
			//nome add 
			
			ide.nome = tabelas.get(indice).getToken();
			//tipo procedure
			ide.tipo = tabelas.get(indice-1).getToken();
			// despois do procedure, verifica se é um identificador
			if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
				// verifico o procedimento
				if(AnalisadorSemantico.comparaProcedimento(ide)) {
				proximo();
				  //depois do identificador deve-se verificar os argumentos
				if (argumento()) {
					return true;
				} else {
					System.out.printf("Erro no Argumento Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;

				}}else {
					System.out.println("erro semantico, problema no procedimento");
				}

			} else if (tabelas.get(indice).getToken().matches("begin")) {
				return true;
			}

		}

		return false;
	}

     // argumentos →
       //   (lista_de_parametros)
      //    | ε
     
	public Boolean argumento() {
		if (tabelas.get(indice).getToken().equals("(")) {
			proximo();
			return listaDeParamentros();

		} else if (tabelas.get(indice).getToken().equals(";")) {
			return true;
		} else {
			System.out.printf("Erro no delimitador Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
					tabelas.get(indice).getToken());
			return false;
		}

	}


	 //lista_de_parametros → 
	//lista_de_identificadores: REGEX_tipo |
	//lista_de_parametros; 
	//lista_de_identificadores: REGEX_tipo
	

	public Boolean listaDeParamentros() {
		//pode ou não ter esse var 
		if (tabelas.get(indice).getToken().matches("var"))
			proximo();
		   //listaDeIdentificadores vai testar se a lista esta correta, caso ela exista.
		if (listaId()) {
			if (tabelas.get(indice).getToken().equals(":")) {
				proximo();
				//pegando as declaracoes
				String tipoDoParametro = tabelas.get(indice).getToken();
				if (tabelas.get(indice).getToken()
						.matches("integer|INTEGER|real|REAL|" + "boolean|BOOLEAN|char|CHAR")) {
					
					//tipo de i que vai ser declarado, declarando o tipo de cada variavel
					AnalisadorSemantico.tipoDasVariaveis(tipoDoParametro);
		
					proximo();
					if (tabelas.get(indice).getToken().equals(")")) {
						proximo();
						if (tabelas.get(indice).getToken().equals(";")) {
							return true;
						} else {
							System.out.printf("Erro no delimitador Linha: %d - Token: %s \n",
									tabelas.get(indice).getLinha(), tabelas.get(indice).getToken());
							return false;
						}
					}
					// para verificar os proximos args
					else if (tabelas.get(indice).getToken().equals(";")) {
						proximo();
						return listaDeParamentros();
					} else {
						System.out.printf("Erro no ) Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
								tabelas.get(indice).getToken());
						return false;
					}
				} else {
					System.out.printf("Erro no tipo Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
				}
			} else {
				System.out.printf("Erro no : - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
			}
		} else {
			System.out.printf("Erro no : - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
					tabelas.get(indice).getToken());
			return false;

		}

	}

	public Boolean comandoComposto() {
		if (tabelas.get(indice).getToken().matches("begin")) {
			// quando encontrar o begin abre o escopo
			AnalisadorSemantico.escopoAberto();
			
			proximo();
			if (comandoOpcional()) {
				if (tabelas.get(indice).getToken().matches("end")) {
					//fecha o escopo
					AnalisadorSemantico.escopoFechado();
					proximo();
					return true;
				} else {
					System.out.printf("É esperado um end - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
				}

			} else {
				System.out.printf("Erro no comando opcional - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
			}
		}

		return false;
	}

	public Boolean comandoOpcional() {

		if (listaDeComandos()) {
			return true;
		} else {

			return true;
		}
	}

	public Boolean listaDeComandos() {
		if (comando()) {
			if (tabelas.get(indice).getToken().equals(";")) {
				// receber um novo comando ou deixa um comando vazio
				AnalisadorSemantico.comandosDesempilhar();
				proximo();
				if (tabelas.get(indice).getToken().matches("end"))
					return true;

				else if (listaDeComandos())
					return true;

			}

		}
		System.out.printf("Erro na lista de comandos - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
				tabelas.get(indice).getToken());
		return false;
	}

	 
	 //comando → variável := expressão 
	 // | ativação_de_procedimento
	//| comando_composto
	 // | if expressão then comando parte_else 
	//| while expressão do comando

	public Boolean comando() {
		Id id1 = new Id(tabelas.get(indice).token,"");
	//	System.out.println("id1 "+ id1.nome);
		if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
			if (ativacaoDeProcedimento()) {
				return true;
			} else {
				//problema proximo
				 proximo();
				if (tabelas.get(indice).getToken().equals(":=")) {
				//	System.out.println("valor aqui "+id1.nome);
					if(AnalisadorSemantico.comparaVariavel(id1)) {
					proximo();
					return expressao();
					}
				else {
						System.out.println("erro semantico no identificador comando");
						return false;
					}
				}
			}
		} else if (comandoComposto()) {
			return true;

		} else if (tabelas.get(indice).getToken().matches("if")) {
			proximo();
			if (expressao()) {
				if(AnalisadorSemantico.tComandos.equals("boolean")) {
				if (tabelas.get(indice).getToken().matches("then")) {
					AnalisadorSemantico.comandosDesempilhar();
					proximo();
					if (comando()) {
						proximo();
						if (tabelas.get(indice).getToken().matches("else")) {
							proximo();
							return comando();
						} else {
							proximo();
							return true;
						}
					}
				}
			}else {
				System.out.println("erro semantico no if");
			}}
			
		} else if (tabelas.get(indice).getToken().matches("while")) {
			proximo();
			if (expressao()) {
				if(AnalisadorSemantico.tComandos.equals("boolean")) {
				if (tabelas.get(indice).getToken().matches("do")) {
					proximo();
					return comando();
				} else {
					System.out.printf("Erro no while - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
				}
			}else {
				System.out.println("erro semantico no while");
				return false;
			}
			}
		}else if(tabelas.get(indice).getToken().matches("case")) {
			proximo();
			if(tabelas.get(indice).getClassificacao().matches("Numero Inteiro")) {
				proximo();
				if(tabelas.get(indice).getToken().matches("of")) {
					proximo();
					if(selecao()) {
						if(tabelas.get(indice).getToken().matches("else")) {
							proximo();
							if(listaDeComandos()) {
								proximo();
								if(tabelas.get(indice).getToken().matches("end")) {
									proximo();
									if(tabelas.get(indice).getToken().matches(";")) {
										return true;
									}
									else {
										System.out.printf("Erro no ; - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
												tabelas.get(indice).getToken());
										return false;
									}
								}else {
									System.out.printf("Erro no end - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
											tabelas.get(indice).getToken());
									return false;
								}
							}
						}
						else {
							proximo();
							return true;
						}
						
					}else {
					System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
					}
				}else {
					System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
							tabelas.get(indice).getToken());
					return false;
					}
					
			}else {
				System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
				}
		}
		else {
			return false;
		}
		return false;
	}
	
	public Boolean ativacaoDeProcedimento() {
		if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
			proximo();
			if (tabelas.get(indice).getToken().equals("(")) {
				Id id2 = new Id(tabelas.get(indice -1).token , "procedure");
				if(AnalisadorSemantico.comparaProcedimento(id2)) {
				proximo();
				if (listaDeExpressoes()) {
					if (tabelas.get(indice).getToken().equals(")")) {
						proximo();
						return true;
					} else {
						System.out.printf("Erro na ativacao - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
								tabelas.get(indice).getToken());
						return false;
					}
				}
			} else {
				System.out.println("erro semantico na ativação do procedimento");
				return false;
			}
		} else {
			indice--;
			return false;
		}
		}
			
		return false;
	}
	public Boolean selecao() {
		if(tabelas.get(indice).getClassificacao().equals("Numero Inteiro")) {
			proximo();
			if(tabelas.get(indice).getToken().equals(":")) {
				proximo();
				if(comando()) {
					if(tabelas.get(indice).getToken().equals(";")) {
						proximo();
						// mudar para zerar o comando
						AnalisadorSemantico.comandosDesempilhar();
						if(tabelas.get(indice).getClassificacao().equals("Numero Inteiro")) {
							return selecao();
							
						}
						else {
							return true;
						}
					}
					 else {
							System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
									tabelas.get(indice).getToken());
							return false;
						}
				}
				 else {
						System.out.printf("Erro na case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
								tabelas.get(indice).getToken());
						return false;
					}
				
			} else {
				System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
			}
		} else {
			System.out.printf("Erro no case - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
					tabelas.get(indice).getToken());
			return false;
		}
	}

	public Boolean expressao() {
		if (expressaoSimples()) {
			if (tabelas.get(indice).getClassificacao().matches("Operador Relacional")) {
				// aqui é um > | < | and | true ...
				//System.out.println("quem é: " +tabelas.get(indice).token);
				AnalisadorSemantico.comparaTipo(tabelas.get(indice).token);
				proximo();
				return expressaoSimples();
			}
			return true;

		}
		return false;
	}

    //  expressão_simples →
    //      termo
    //     | sinal termo
     //     | expressão_simples op_aditivo termo

	public Boolean expressaoSimples() {
		if (termo()) {
			if (tabelas.get(indice).getClassificacao().matches("Operador Aditivo")) {
				proximo();
				return expressaoSimples();
			}
			return true;
		} 
		  //o termo pode recebe um sinal negativo
		else if (tabelas.get(indice).getToken().matches("[+\\-]")) {
			return termo();
		}

		System.out.printf("É esperado um termo - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
				tabelas.get(indice).getToken());
		return false;

	}
    // termo →
    //     fator
   //     | termo op_multiplicativo fator
	public Boolean termo() {
		if (fator()) {
			proximo();
			if (tabelas.get(indice).getClassificacao().matches("Operador Multiplicativo")) {
				AnalisadorSemantico.comparaTipo(tabelas.get(indice).token);
				proximo();
				return termo();
			}
			// se ele for apenas fator 
			return true;
		}
		System.out.printf("Erro no termo - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
				tabelas.get(indice).getToken());
		return false;

	}
   //fator →
   //     id
   //      | id(lista_de_expressões)
   //      | num_int
   //      | num_real
   //      | true
   //      | false
   //      | (expressão)
   //      | not fator

	public Boolean fator() {
		
		Id id4 = new Id(tabelas.get(indice).token,"");
		//essa parte verifica se é um identificador ou numero inteiro ou real ou true ou false e assim por diante.
		if (tabelas.get(indice).getClassificacao().matches("Identificador")) {
			AnalisadorSemantico.comparaVariavel(id4);
			proximo();
			if (tabelas.get(indice).getToken().equals("(")) {
				proximo();
				if (listaDeExpressoes()) {
					proximo();
					if (tabelas.get(indice).getToken().equals(")")) {
						proximo();
						return true;
					}
				}
			}
			indice = indice - 1;
			return true;
		} else if (tabelas.get(indice).getClassificacao().matches("Numero Inteiro")) {
			AnalisadorSemantico.comparaTipo("integer");
			return true;
		} else if (tabelas.get(indice).getClassificacao().matches("Numero Real")) {
			AnalisadorSemantico.comparaTipo("real");
			return true;
		} else if (tabelas.get(indice).getToken().matches("true")) {
			AnalisadorSemantico.comparaTipo("boolean");
			return true;
		}
		else if (tabelas.get(indice).getToken().matches("false")) {
			AnalisadorSemantico.comparaTipo("boolean");
			return true;
		} else if (tabelas.get(indice).getToken().equals("(")) {
			proximo();
			if (expressao()) {
				return tabelas.get(indice).getToken().equals(")");
			} else {
				System.out.printf("Erro no fator - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
						tabelas.get(indice).getToken());
				return false;
			}

		} else if (tabelas.get(indice).getToken().matches("not")) {
			proximo();
			return fator();
		} else {
			System.out.printf("Erro no fator - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
					tabelas.get(indice).getToken());
			return false;
		}
	}
    //lista_de_expressões →
    //      expressão
    //      | lista_de_expressões, expressão
	public Boolean listaDeExpressoes() {
		if (expressao()) {
			proximo();
			if (tabelas.get(indice).getToken().equals(","))
				return listaDeExpressoes();

			indice = indice - 1;
			return true;

		}
		System.out.printf("Erro na lista de expressoes - Linha: %d - Token: %s \n", tabelas.get(indice).getLinha(),
				tabelas.get(indice).getToken());
		return false;
	}

}
