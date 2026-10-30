public class OperacaoFinanceira {
  private double valor;
  private String descricao;
  private CalculaDiaSemana data;

  public void setData(int d, int m, int an) {
    data = new CalculaDiaSemana(d, m, an);
  }

  public void setValor(double v) {
    valor = v;
  }

  public void setDescricao(String d) {
    descricao = d;
  }

  public double getValor() {
    return valor;
  }

  public String getDescricao() {
    return descricao;
  }

  public String getData() {
    String dia = String.valueOf(data.getDia());
    String mes = String.valueOf(data.getMes());
    String ano = String.valueOf(data.getAno());

    return String.format("%s / %s / %s", dia, mes, ano);
  }

  public OperacaoFinanceira() {
    valor = 0;
    descricao = "";
    data = new CalculaDiaSemana();
  }

  public void setCadastrarOperacao(int di, int me, int an, double va, String des) {
    data = new CalculaDiaSemana(di, me, an);
    valor = va;
    descricao = des;
  }
}
