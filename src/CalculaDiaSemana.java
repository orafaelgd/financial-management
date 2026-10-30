public class CalculaDiaSemana {
  private int diaSemana;
  private int dia;
  private int mes;
  private int ano;

  public void setDia(int d) {
    dia = d;
  }

  public void setMes(int m) {
    mes = m;
  }

  public void setAno(int a) {
    ano = a;
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

  public CalculaDiaSemana() {
    diaSemana = 0;
    dia = 0;
    mes = 0;
    ano = 0;
  }

  public CalculaDiaSemana(int d, int m, int an) {
    dia = d;
    mes = m;
    ano = an;
  }

  public int diaSemana(int dia, int mes, int ano) {
    int A, B, C, D, E, F, G, H, I;

    A = (int) ((12 - mes) / 10);
    B = ano - A;
    C = mes + (12 * A);
    D = (int) (B / 100);
    E = (int) (D / 4);
    F = 2 - D + E;
    G = (int) (365.25 * B);
    H = (int) (30.6001 * (C + 1));
    I = (int) ((F + G) + (H + dia) + 5);

    diaSemana = I % 7;

    return diaSemana;
  }

  public int diaSemana() {
    return diaSemana(this.dia, this.mes, this.ano);
  }

  String diaSemanaString() {

    int result = diaSemana();

    String[] dias = {
        "Sábado",
        "Domingo",
        "Segunda-feira",
        "Terça-feira",
        "Quarta-feira",
        "Quinta-feira",
        "Sexta-feira"
    };

    return dias[result];
  }
}
