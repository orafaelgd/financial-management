import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;

public class Arquivo {
  private final MultiOperacoesFinanceiras cadastro;

  public Arquivo(MultiOperacoesFinanceiras cadastro) {
    this.cadastro = cadastro;
  }

  public int arquivoGravarDados(String nm) {
    AplicacaoFinanceira aplicacao = cadastro.getAplicacao();
    OperacaoFinanceira[] operacoes = cadastro.getOperacao();
    int nReg = cadastro.getQtdOperacoes();
    if (nm == null || aplicacao == null || operacoes == null
        || nReg < 0 || nReg > 100 || nReg > operacoes.length) {
      return -1;
    }
    // Confere as datas antes de abrir e sobrescrever o arquivo.
    try {
      for (int pos = 0; pos < nReg; pos++) {
        if (operacoes[pos] == null) {
          return -1;
        }
        operacoes[pos].getDataOperacao();
      }
    } catch (DateTimeException e) {
      return -1;
    }

    try (FileWriter arq = new FileWriter(nm, StandardCharsets.UTF_8);
        PrintWriter gravarArq = new PrintWriter(arq)) {
      gravarArq.println("FINANCEIRO-1");
      gravarArq.println(textoParaLinha(aplicacao.getNOMEAPLICACAO()));
      gravarArq.println(textoParaLinha(aplicacao.getNUMEROAPLICACAPFINANCEIRA()));
      gravarArq.println(aplicacao.getVALORAPLICADO());
      gravarArq.println(aplicacao.getVALORRESGATADO());
      gravarArq.println(aplicacao.getVALORATUALBRUTO());
      gravarArq.println(aplicacao.getVALORATUALLIQUIDO());
      gravarArq.println(aplicacao.getRENDIMENTOATUALBRUTO());
      gravarArq.println(aplicacao.getRENDIMENTOATUALLIQUIDO());
      gravarArq.println(aplicacao.getTAXADIARIA());
      gravarArq.println(aplicacao.getTAXAMENSAL());
      gravarArq.println(aplicacao.getTIPORENDIMENTO());
      gravarArq.println(aplicacao.getTEMPOFINALINVESTIMENTO());
      gravarArq.println(aplicacao.getTEMPOMINIMOPERMANENCIA());
      gravarArq.println(aplicacao.getTIPORESGATE());
      gravarArq.println(aplicacao.getIOF());
      gravarArq.println(aplicacao.getTIPOIMPOSTO());
      gravarArq.println(aplicacao.getTEMPOATUALIZADOAPLICACAO());
      gravarArq.println(aplicacao.getTEMPOATUALIZADOEFETIVO());
      gravarArq.println(aplicacao.getIMPOSTOATUALDEVIDO());
      gravarArq.println(aplicacao.getDIA());
      gravarArq.println(aplicacao.getMES());
      gravarArq.println(aplicacao.getANO());
      gravarArq.println(aplicacao.getSTATUS());
      gravarArq.println(aplicacao.getDiasCalculos());
      LocalDate ultimaData = aplicacao.getUltimaDataProcessada();
      gravarArq.println(ultimaData == null ? "" : ultimaData.toString());
      gravarArq.println(nReg);

      for (int pos = 0; pos < nReg; pos++) {
        gravarArq.println(operacoes[pos].getDataOperacao());
        gravarArq.println(operacoes[pos].getValor());
        gravarArq.println(textoParaLinha(operacoes[pos].getDescricao()));
      }
    } catch (IOException e) {
      return -1;
    }
    return nReg;
  }

  public int arquivoLerDados(String nm) {
    if (nm == null || cadastro.getAplicacao() == null) {
      return -1;
    }
    AplicacaoFinanceira aplicacao = new AplicacaoFinanceira();
    OperacaoFinanceira[] operacoes = new OperacaoFinanceira[100];
    int nReg;

    try (BufferedReader arq = new BufferedReader(new FileReader(nm, StandardCharsets.UTF_8))) {
      aplicacao.NOMEAPLICACAO = linhaParaTexto(lerLinha(arq));
      aplicacao.NUMEROAPLICACAPFINANCEIRA = linhaParaTexto(lerLinha(arq));
      aplicacao.VALORAPLICADO = lerValor(arq);
      aplicacao.VALORRESGATADO = lerValor(arq);
      aplicacao.VALORATUALBRUTO = lerValor(arq);
      aplicacao.VALORATUALLIQUIDO = lerValor(arq);
      aplicacao.RENDIMENTOATUALBRUTO = lerValor(arq);
      aplicacao.RENDIMENTOATUALLIQUIDO = lerValor(arq);
      aplicacao.TAXADIARIA = lerValor(arq);
      aplicacao.TAXAMENSAL = lerValor(arq);
      aplicacao.TIPORENDIMENTO = Integer.parseInt(lerLinha(arq));
      aplicacao.TEMPOFINALINVESTIMENTO = Integer.parseInt(lerLinha(arq));
      aplicacao.TEMPOMINIMOPERMANENCIA = Integer.parseInt(lerLinha(arq));
      aplicacao.TIPORESGATE = Integer.parseInt(lerLinha(arq));
      aplicacao.IOF = Integer.parseInt(lerLinha(arq));
      aplicacao.TIPOIMPOSTO = Integer.parseInt(lerLinha(arq));
      aplicacao.TEMPOATUALIZADOAPLICACAO = Integer.parseInt(lerLinha(arq));
      aplicacao.TEMPOATUALIZADOEFETIVO = Integer.parseInt(lerLinha(arq));
      aplicacao.IMPOSTOATUALDEVIDO = lerValor(arq);
      aplicacao.DIA = Integer.parseInt(lerLinha(arq));
      aplicacao.MES = Integer.parseInt(lerLinha(arq));
      aplicacao.ANO = Integer.parseInt(lerLinha(arq));
      aplicacao.STATUS = Integer.parseInt(lerLinha(arq));
      aplicacao.diasCalculos = Integer.parseInt(lerLinha(arq));
      String ultimaData = lerLinha(arq);
      aplicacao.setUltimaDataProcessada(ultimaData.isEmpty() ? null : LocalDate.parse(ultimaData));
      nReg = Integer.parseInt(lerLinha(arq));
      if (nReg < 0 || nReg > operacoes.length || aplicacao.NOMEAPLICACAO == null
          || aplicacao.NUMEROAPLICACAPFINANCEIRA == null) {
        return -1;
      }

      for (int pos = 0; pos < operacoes.length; pos++) {
        operacoes[pos] = new OperacaoFinanceira();
        if (pos < nReg) {
          LocalDate data = LocalDate.parse(lerLinha(arq));
          double valor = lerValor(arq);
          String descricao = linhaParaTexto(lerLinha(arq));
          operacoes[pos].setCadastrarOperacao(data.getDayOfMonth(), data.getMonthValue(),
              data.getYear(), valor, descricao);
        }
      }
    } catch (IOException | IllegalArgumentException | DateTimeException e) {
      return -1;
    }

    try {
      aplicacao.registrarDadosLidos(cadastro.getAplicacao());
    } catch (IllegalArgumentException e) {
      return -1;
    }
    cadastro.setAplicacao(aplicacao);
    cadastro.setOperacao(operacoes);
    cadastro.setQtdOperacoes(nReg);
    return nReg;
  }

  private String lerLinha(BufferedReader arq) throws IOException {
    String linha = arq.readLine();

    return linha;
  }

  private double lerValor(BufferedReader arq) throws IOException {
    double valor = Double.parseDouble(lerLinha(arq));

    return valor;
  }

  private String textoParaLinha(String texto) {
    return texto == null ? "\\0"
        : texto.replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
  }

  private String linhaParaTexto(String linha) throws IOException {
    StringBuilder texto = new StringBuilder();

    for (int i = 0; i < linha.length(); i++) {
      char caractere = linha.charAt(i);
      if (caractere == '\\') {
        char escape = linha.charAt(i);
        switch (escape) {
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
