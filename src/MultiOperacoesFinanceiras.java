import java.time.LocalDate;

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

  public void atualizarValores(int pos, int d, int me, int an) {
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

  public String getExtrato() {
    LocalDate fim = LocalDate.now();
    LocalDate inicio = fim.minusDays(29);

    return getExtrato(inicio, fim);
  }

  public double getSaldo() {
    return aplicacao.getVALORATUALLIQUIDO();
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
