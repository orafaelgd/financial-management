import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiOperacoesFinanceiras {
  private OperacaoFinanceira[] operacao = new OperacaoFinanceira[100];
  private AplicacaoFinanceira aplicacao;
  private int qtdOperacoes;

  public MultiOperacoesFinanceiras() {
    for (int i = 0; i < 100; i++) {
      operacao[i] = new OperacaoFinanceira();
    }

    aplicacao = new AplicacaoFinanceira();
  }

  public void setCadastroDadosOperacao(int di, int me, int an, String nmap, String nap,
      double vlap, double txdia, double txmes,
      int tiporend, int tmpfinal, int tempomin,
      int tiporesg, int tipoiof, int tipoimposto, String des) {

    aplicacao.setCadastro(nmap, nap, vlap, txdia, txmes, tiporend,
        tmpfinal, tempomin, tiporesg, tipoiof, tipoimposto, di, me, an);

    registrarOperacao(di, me, an, vlap, des);
  }

  public void setCadastroDadosOperacao(int di, int me, int an, double valor, String des) {
    OperacaoFinanceira lancamento = operacao[qtdOperacoes];
    aplicacao.atualizarDiaAplicação(di, me, an);

    lancamento.setCadastrarOperacao(di, me, an, valor, des);
    qtdOperacoes++;
  }

  private void registrarOperacao(int di, int me, int an, double valor, String des) {
    operacao[qtdOperacoes].setCadastrarOperacao(di, me, an, valor, des);
    qtdOperacoes++;
  }

  public void atualizarSaldo(int d, int me, int an) {
    OperacaoFinanceira lancamento = operacao[qtdOperacoes];
    aplicacao.atualizarDiaAplicação(d, me, an);

    lancamento.setCadastrarOperacao(d, me, an, aplicacao.getVALORATUALBRUTO(),
        "Saldo bruto atualizado");
    qtdOperacoes++;
  }

  public String getExtrato(LocalDate inicio, LocalDate fim) {
    StringBuilder extrato = new StringBuilder();

    extrato.append("Aplicação: ")
        .append(aplicacao.getNOMEAPLICACAO())
        .append("\nNúmero: ")
        .append(aplicacao.getNUMEROAPLICACAPFINANCEIRA())
        .append("\nPeríodo: ")
        .append(inicio)
        .append(" até ")
        .append(fim)
        .append("\n\n");

    boolean encontrou = false;

    for (int i = 0; i < qtdOperacoes; i++) {
      LocalDate dataOperacao = operacao[i].getDataOperacao();

      if (!dataOperacao.isBefore(inicio)
          && !dataOperacao.isAfter(fim)) {

        extrato.append(operacao[i].getData())
            .append(" | ")
            .append(operacao[i].getDescricao())
            .append(" | ")
            .append(operacao[i].getValor())
            .append("\n");

        encontrou = true;
      }
    }

    if (!encontrou) {
      extrato.append("Nenhuma operação no período.\n");
    }

    return extrato.toString();
  }

  public int realizarAporte(int dia, int mes, int ano, double valor) {
    if (qtdOperacoes >= operacao.length) {
      throw new IllegalStateException("Histórico de operações cheio");
    }

    aplicacao.atualizarDiaAplicação(dia, mes, ano);
    int resultado = aplicacao.aporteValor(valor);

    if (resultado == 1) {
      registrarOperacao(dia, mes, ano, valor, "Aporte");
    }

    return resultado;
  }

  public String getExtrato() {
    LocalDate fim = LocalDate.now();
    LocalDate inicio = fim.minusDays(29);

    return getExtrato(inicio, fim);
  }

  public double getSaldo() {
    return aplicacao.getVALORATUALLIQUIDO();
  }

  public int arquivoSalvarDados(String nm) {
    if (nm == null || aplicacao == null || operacao == null
        || qtdOperacoes < 0 || qtdOperacoes > 100 || qtdOperacoes > operacao.length) {
      return -1;
    }

    List<String> linhas = new ArrayList<>(Arrays.asList(
        "FINANCEIRO-1",
        aplicacao.getNOMEAPLICACAO(),
        aplicacao.getNUMEROAPLICACAPFINANCEIRA(),
        String.valueOf(aplicacao.getVALORAPLICADO()),
        String.valueOf(aplicacao.getVALORRESGATADO()),
        String.valueOf(aplicacao.getVALORATUALBRUTO()),
        String.valueOf(aplicacao.getVALORATUALLIQUIDO()),
        String.valueOf(aplicacao.getRENDIMENTOATUALBRUTO()),
        String.valueOf(aplicacao.getRENDIMENTOATUALLIQUIDO()),
        String.valueOf(aplicacao.getTAXADIARIA()),
        String.valueOf(aplicacao.getTAXAMENSAL()),
        String.valueOf(aplicacao.getTIPORENDIMENTO()),
        String.valueOf(aplicacao.getTEMPOFINALINVESTIMENTO()),
        String.valueOf(aplicacao.getTEMPOMINIMOPERMANENCIA()),
        String.valueOf(aplicacao.getTIPORESGATE()),
        String.valueOf(aplicacao.getIOF()),
        String.valueOf(aplicacao.getTIPOIMPOSTO()),
        String.valueOf(aplicacao.getTEMPOATUALIZADOAPLICACAO()),
        String.valueOf(aplicacao.getTEMPOATUALIZADOEFETIVO()),
        String.valueOf(aplicacao.getIMPOSTOATUALDEVIDO()),
        String.valueOf(aplicacao.getDIA()),
        String.valueOf(aplicacao.getMES()),
        String.valueOf(aplicacao.getANO()),
        String.valueOf(aplicacao.getSTATUS()),
        String.valueOf(aplicacao.getDiasCalculos()),
        aplicacao.getUltimaDataProcessada() == null ? "" : aplicacao.getUltimaDataProcessada().toString(),
        String.valueOf(qtdOperacoes)));

    try {
      for (int pos = 0; pos < qtdOperacoes; pos++) {
        if (operacao[pos] == null) {
          return -1;
        }
        linhas.add(operacao[pos].getDataOperacao().toString());
        linhas.add(String.valueOf(operacao[pos].getValor()));
        linhas.add(operacao[pos].getDescricao());
      }

      Arquivo.arquivoGravarDados(nm, linhas);
      return qtdOperacoes;
    } catch (IOException | DateTimeException e) {
      return -1;
    }
  }

  public int arquivoLerDados(String nm) {
    return arquivoLerDados(nm, true);
  }

  // Cliente confirma os numeros depois de ler todas as aplicacoes.
  int arquivoLerDados(String nm, boolean registrarNumero) {
    if (nm == null || aplicacao == null) {
      return -1;
    }

    AplicacaoFinanceira dados = new AplicacaoFinanceira();
    OperacaoFinanceira[] operacoesLidas = new OperacaoFinanceira[100];
    int quantidade;

    try {
      List<String> linhas = Arquivo.arquivoLerDados(nm);
      if (linhas.size() < 27 || !"FINANCEIRO-1".equals(linhas.get(0))) {
        return -1;
      }

      int pos = 1;
      dados.NOMEAPLICACAO = linhas.get(pos++);
      dados.NUMEROAPLICACAPFINANCEIRA = linhas.get(pos++);
      dados.VALORAPLICADO = Double.parseDouble(linhas.get(pos++));
      dados.VALORRESGATADO = Double.parseDouble(linhas.get(pos++));
      dados.VALORATUALBRUTO = Double.parseDouble(linhas.get(pos++));
      dados.VALORATUALLIQUIDO = Double.parseDouble(linhas.get(pos++));
      dados.RENDIMENTOATUALBRUTO = Double.parseDouble(linhas.get(pos++));
      dados.RENDIMENTOATUALLIQUIDO = Double.parseDouble(linhas.get(pos++));
      dados.TAXADIARIA = Double.parseDouble(linhas.get(pos++));
      dados.TAXAMENSAL = Double.parseDouble(linhas.get(pos++));
      dados.TIPORENDIMENTO = Integer.parseInt(linhas.get(pos++));
      dados.TEMPOFINALINVESTIMENTO = Integer.parseInt(linhas.get(pos++));
      dados.TEMPOMINIMOPERMANENCIA = Integer.parseInt(linhas.get(pos++));
      dados.TIPORESGATE = Integer.parseInt(linhas.get(pos++));
      dados.IOF = Integer.parseInt(linhas.get(pos++));
      dados.TIPOIMPOSTO = Integer.parseInt(linhas.get(pos++));
      dados.TEMPOATUALIZADOAPLICACAO = Integer.parseInt(linhas.get(pos++));
      dados.TEMPOATUALIZADOEFETIVO = Integer.parseInt(linhas.get(pos++));
      dados.IMPOSTOATUALDEVIDO = Double.parseDouble(linhas.get(pos++));
      dados.DIA = Integer.parseInt(linhas.get(pos++));
      dados.MES = Integer.parseInt(linhas.get(pos++));
      dados.ANO = Integer.parseInt(linhas.get(pos++));
      dados.STATUS = Integer.parseInt(linhas.get(pos++));
      dados.diasCalculos = Integer.parseInt(linhas.get(pos++));
      String ultimaData = linhas.get(pos++);
      dados.setUltimaDataProcessada(ultimaData.isEmpty() ? null : LocalDate.parse(ultimaData));
      quantidade = Integer.parseInt(linhas.get(pos++));

      if (quantidade < 0 || quantidade > operacoesLidas.length
          || linhas.size() != 27 + quantidade * 3
          || dados.NOMEAPLICACAO == null || dados.NUMEROAPLICACAPFINANCEIRA == null) {
        return -1;
      }

      for (int i = 0; i < operacoesLidas.length; i++) {
        operacoesLidas[i] = new OperacaoFinanceira();
        if (i < quantidade) {
          LocalDate data = LocalDate.parse(linhas.get(pos++));
          double valor = Double.parseDouble(linhas.get(pos++));
          String descricao = linhas.get(pos++);
          operacoesLidas[i].setCadastrarOperacao(data.getDayOfMonth(), data.getMonthValue(),
              data.getYear(), valor, descricao);
        }
      }

      // So substitui os dados atuais depois de conferir todo o arquivo.
      if (registrarNumero) {
        dados.registrarDadosLidos(aplicacao);
      }
    } catch (IOException | IllegalArgumentException | DateTimeException | NullPointerException e) {
      return -1;
    }

    aplicacao = dados;
    operacao = operacoesLidas;
    qtdOperacoes = quantidade;
    return quantidade;
  }

  public AplicacaoFinanceira getAplicacao() {
    return aplicacao;
  }

  public void setAplicacao(AplicacaoFinanceira aplicacao) {
    this.aplicacao = aplicacao;
  }

  public OperacaoFinanceira[] getOperacao() {
    return operacao;
  }

  public void setOperacao(OperacaoFinanceira[] operacao) {
    this.operacao = operacao;
  }

  public int getQtdOperacoes() {
    return qtdOperacoes;
  }

  public void setQtdOperacoes(int qtdOperacoes) {
    this.qtdOperacoes = qtdOperacoes;
  }

}
