package lexico;

import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;

import semantico.AnalisadorSemantico;
import sintatico.AnalisadorSintatico;


public class App {
	public static String decodificaOperacao(String s) {

		String Demo = s;
		StringTokenizer Tok = new StringTokenizer(Demo, s);

		while (Tok.hasMoreElements()) {
			// System.out.println(Tok.nextElement());
			s = Tok.nextToken();

		}
		return s;

	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Arquivo a = new Arquivo();
		// String an = new LinkedList<String>(Arquivo.carregarCodigo());
		AnalisadorSintatico sintatico= new AnalisadorSintatico();
		LinkedList<Simbolo> k = new LinkedList<>();
		// System.out.println(Arquivo.memoria());

		// System.out.println(a.memoria());
       
		AnalisadorExpressao b = new AnalisadorExpressao();
		AnalisadorSemantico o= new AnalisadorSemantico();

		// for (int i = 0; i < a.memoria().size(); i++) {

		// System.out.println(decodificaOperacao((String) a.memoria().get(i)));
		b.analisar(k, a.memoria());
		
        sintatico.analisadorSintatico(k, b.retorno());
        
        o.mostrarPilha();

		// }

		//

	}

}
