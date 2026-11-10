import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DadosPessoais {
  private String nome;
  private String cpf;
  private String celular;

  DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  // === GET ATRIBUTOS === \\
  public String getNome() {
    return nome;
  }

  public String getCelular() {
    return celular;
  }

  public String getCpf() {
    return cpf;
  }

  // ===============

  // === SET ATRIBUTOS === \\
  public void setNome(String nm) {
    nome = nm;
  }

  public void setCelular(String cl) {
    celular = cl;
  }

  public void setCpf(String cp) {
    if (validaCPF(cp) != true) {
      System.out.println("CPF inválido");
      return;
    }

    cpf = cp;
  }
  // ===============

  DadosPessoais() {
    nome = "";
    cpf = "";
    celular = "";
  }

  public void cadastrarDados(String nm, String cp, String cel) {

    String cpfFormatado = cp.replaceAll("\\D", "");
    if (validaCPF(cpfFormatado) != true) {
      System.out.println("\nCPF inválida!!!!!!");
      return;
    }

    nome = nm;
    cpf = cpfFormatado;
    celular = cel;
  }

  public void alterarDadosPessoais(String variavel, String dado) {
    switch (variavel) {
      case "nome":
        nome = dado;
        break;
      case "cpf":
        if (validaCPF(dado) != true) {
          System.out.println("CPF inválido...");
          return;
        }

        cpf = dado;
        break;
      case "celular":
        celular = dado;
        break;
      default:
        break;
    }
  }

  @Override
  public String toString() {
    return String.format("nome: %s | cpf: %s | endereço: %s | Data Nascimento: %s | celular: %s", nome, cpf, celular);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nome, celular, cpf);
  }

  @Override
  public boolean equals(Object ob) {
    if (this == ob)
      return true;

    if (ob == null)
      return false;

    if (getClass() != ob.getClass())
      return false;

    if (!toString().equals(ob.toString()))
      return false;

    return true;
  }

  private boolean validaCPF(String cp) {
    int soma1, soma2;
    int digito1, digito2;
    boolean status;

    if (!cp.matches("[0-9]{11}") || cp.matches("([0-9])\\1{10}")) {
      status = false;
      return status;
    }

    soma1 = 0;
    for (int i = 0; i < 9; i++) {
      soma1 = soma1 + ((int) (cp.charAt(i) - '0') * (10 - i));
    }
    digito1 = (soma1 * 10) % 11;
    if (digito1 == 10)
      digito1 = 0;

    if (digito1 != (int) (cp.charAt(9)) - '0') {
      status = false;
      return status;
    }

    soma2 = 0;
    for (int i = 0; i < 10; i++) {
      soma2 = soma2 + ((int) (cp.charAt(i) - '0') * (11 - i));
    }
    digito2 = (soma2 * 10) % 11;
    if (digito2 == 10)
      digito2 = 0;

    if (digito2 != (int) (cp.charAt(10)) - '0') {
      status = false;
      return status;
    } else {
      status = true;
    }

    return status;
  }
}
