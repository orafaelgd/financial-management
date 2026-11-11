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
    if (nm == null || nm.trim().isEmpty()) {
      throw new IllegalArgumentException("Informe o nome do cliente.");
    }
    nome = nm;
  }

  public void setCelular(String cl) {
    Objects.requireNonNull(cl, "Informe o celular.");
    celular = cl;
  }

  public void setCpf(String cp) {
    cpf = normalizarCpf(cp);
  }
  // ===============

  DadosPessoais() {
    nome = "";
    cpf = "";
    celular = "";
  }

  public void cadastrarDados(String nm, String cp, String cel) {
    String cpfFormatado = normalizarCpf(cp);
    if (nm == null || nm.trim().isEmpty() || cel == null) {
      throw new IllegalArgumentException("Informe nome e celular do cliente.");
    }

    nome = nm;
    cpf = cpfFormatado;
    celular = cel;
  }

  public void alterarDadosPessoais(String variavel, String dado) {
    switch (variavel) {
      case "nome":
        setNome(dado);
        break;
      case "cpf":
        setCpf(dado);
        break;
      case "celular":
        setCelular(dado);
        break;
      default:
        break;
    }
  }

  @Override
  public String toString() {
    return String.format("nome: %s | cpf: %s | celular: %s", nome, cpf, celular);
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

    DadosPessoais outro = (DadosPessoais) ob;
    return Objects.equals(nome, outro.nome)
        && Objects.equals(cpf, outro.cpf)
        && Objects.equals(celular, outro.celular);
  }

  private String normalizarCpf(String cp) {
    if (cp == null) {
      throw new IllegalArgumentException("Informe um CPF válido.");
    }
    String numero = cp.replaceAll("\\D", "");
    if (!validaCPF(numero)) {
      throw new IllegalArgumentException("CPF inválido.");
    }
    return numero;
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
