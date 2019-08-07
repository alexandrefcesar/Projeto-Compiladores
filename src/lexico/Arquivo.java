package lexico;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Arquivo {

	public static ArrayList arquivoDados() {
		ArrayList lista = new ArrayList();
		File arquivo = new File("arquivo.txt");
		String linha;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo)));
			while (br.ready()) {
				linha = br.readLine();
				// System.out.println(linha);
				lista.add(linha);
				// System.out.println(lista.get(0));

				// System.out.println("elementos"+ lista.hashCode());
			}
			// System.out.println(lista.get(3));
			// System.out.println("tamanho da lista" + lista.size());
			br.close();
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
		return lista;

	}

	public static ArrayList memoria() {
		ArrayList lista = arquivoDados();
		return lista;
	}

	public static boolean Write(String Caminho, String Texto) {
		try {
			FileWriter arq = new FileWriter(Caminho);
			PrintWriter gravarArq = new PrintWriter(arq);
			gravarArq.println(Texto);
			gravarArq.close();
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
}