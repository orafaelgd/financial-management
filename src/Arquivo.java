import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Arquivo {

  public static void arquivoGravarDados(String nomeArquivo, List<String> linhas) throws IOException {
    try (PrintWriter gravador = new PrintWriter(
        new FileWriter(nomeArquivo, StandardCharsets.UTF_8))) {
      for (String linha : linhas) {
        gravador.println(textoParaLinha(linha));
      }

      if (gravador.checkError()) {
        throw new IOException("Erro ao gravar o arquivo.");
      }
    }
  }

  public static List<String> arquivoLerDados(String nomeArquivo) throws IOException {
    List<String> linhas = new ArrayList<>();

    try (BufferedReader leitor = new BufferedReader(
        new FileReader(nomeArquivo, StandardCharsets.UTF_8))) {
      String linha;
      while ((linha = leitor.readLine()) != null) {
        linhas.add(linhaParaTexto(linha));
      }
    }

    return linhas;
  }

  // Preserva textos com quebras de linha e barras no formato de uma linha por dado.
  private static String textoParaLinha(String texto) {
    return texto == null ? "\\0"
        : texto.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
  }

  private static String linhaParaTexto(String linha) throws IOException {
    if (linha.equals("\\0")) {
      return null;
    }

    StringBuilder texto = new StringBuilder();
    for (int i = 0; i < linha.length(); i++) {
      char caractere = linha.charAt(i);
      if (caractere == '\\') {
        if (++i >= linha.length()) {
          throw new IOException("Texto invalido.");
        }
        switch (linha.charAt(i)) {
          case 'n': caractere = '\n'; break;
          case 'r': caractere = '\r'; break;
          case '\\': caractere = '\\'; break;
          default: throw new IOException("Texto invalido.");
        }
      }
      texto.append(caractere);
    }
    return texto.toString();
  }
}
