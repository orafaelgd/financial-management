public class OperacaoFinanceira {
  private int dia;
  private int mes;
  private int ano;

  private double valor;
  private String descricao;

  public void setDia(int d) {
    dia = d;
  }

  public void setMes(int m) {
    mes = m;
  }

  public void setAno(int a) {
    ano = a;
  }

  public void setValor(double v) {
    valor = v;
  }

  public void setDescricao(String d) {
    descricao = d;
  }

  public int getDia() {
    return dia;
  }

  public int getMes() {
    return mes;
  }

  public int getAno() {
    return ano;
  }

  public double getValor() {
    return valor;
  }

  public String getDescricao() {
    return descricao;
  }

  public OperacaoFinanceira() {
    dia = 0;
    mes = 0;
    ano = 0;
    valor = 0;
    descricao = "";
  }

  public void setCadastrarOperacao(int di, int me, int an, double va, String des) {
    dia = di;
    mes = me;
    ano = an;
    valor = va;
    descricao = des;
  }
}
