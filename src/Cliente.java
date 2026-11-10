import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Cliente {
  DadosPessoais cliente = new DadosPessoais();
  MultiOperacoesFinanceiras[] aplicacoesFinanceiras = new MultiOperacoesFinanceiras[50];
  int numAgencia, numConta;
  int nRegAplicacoes;
  double saldoConta;

  public Cliente() {
    cliente = new DadosPessoais();
    numAgencia = -1;
    numConta = -1;
    nRegAplicacoes = 0;
    saldoConta = -1;

    for (int pos = 0; pos < 50; pos++) {
      aplicacoesFinanceiras[pos] = new MultiOperacoesFinanceiras();
    }
  }

  public void setCadastroAplicacao(int dia, int mes, int ano,
      int diaOpera, int mesOpera, int anoOpera,
      String nomeAplicacao, String numAplicacao,
      double valorAplicacao, double taxaDiaria, double taxaMes,
      int tipoDeRenda, int tempoFinalInvestimento, int tempoMinimoPermanencia,
      int tipoResgate, int Iof, int tipoImposto, String descricao) {

    if (nRegAplicacoes >= aplicacoesFinanceiras.length) {
      throw new IllegalStateException("Limite de 50 aplicações atingido.");
    }

    aplicacoesFinanceiras[nRegAplicacoes].setCadastroDadosOperacao(
        diaOpera, mesOpera, anoOpera, nomeAplicacao, numAplicacao,
        valorAplicacao, taxaDiaria, taxaMes,
        tipoDeRenda, tempoFinalInvestimento, tempoMinimoPermanencia,
        tipoResgate, Iof, tipoImposto, descricao);

    nRegAplicacoes++;

  }

  public void setCadastroCliente(String nom, String cpf, String celu, int numeroAgencia, int numeroConta,
      double saldo) {
    cliente.cadastrarDados(nom, cpf, celu);

    numAgencia = numeroAgencia;
    numConta = numeroConta;
    saldoConta = saldo;
  }

  public void realizarAporte(int pos, int di, int me, int an, double valor) {

    if (valor > saldoConta) {
      throw new IllegalArgumentException("Valor superior ao saldo da Conta!!");
    }

    int resultado = aplicacoesFinanceiras[pos].realizarAporte(di, me, an, valor);

    if (resultado == 1) {
      saldoConta -= valor;
    }
  }

  public void atualizarAplicacao(int di, int me, int ano) {
    for (int pos = 0; pos < nRegAplicacoes; pos++) {
      aplicacoesFinanceiras[pos].atualizarSaldo(di, me, ano);
    }
  }

  public String getExtrato(int pos) {
    if (pos < 0 || pos >= nRegAplicacoes) {
      throw new IllegalArgumentException("Aplicação inválida...");
    }

    return aplicacoesFinanceiras[pos].getExtrato();
  }

  public String getExtrato() {
    if (nRegAplicacoes == 0) {
      return "Nenhuma aplicação cadastro";
    }

    StringBuilder extratoTotal = new StringBuilder();

    for (int pos = 0; pos < nRegAplicacoes; pos++) {
      extratoTotal.append(getExtrato(pos)).append("\n");
    }

    return extratoTotal.toString();
  }

  public String getExtrato(int pos, LocalDate ini, LocalDate fi) {
    if (ini.isAfter(fi)) {
      throw new IllegalArgumentException("Período inválido");
    }

    return aplicacoesFinanceiras[pos].getExtrato(ini, fi);
  }

  public String getExtrato(LocalDate ini, LocalDate fi) {
    if (ini.isAfter(fi)) {
      throw new IllegalArgumentException("Período inválido");
    }

    if (nRegAplicacoes == 0) {
      return "Nenhuma aplicação cadastrada";
    }

    StringBuilder extratoTotal = new StringBuilder();

    for (int pos = 0; pos < nRegAplicacoes; pos++) {
      extratoTotal.append(getExtrato(pos, ini, fi)).append("\n");
    }

    return extratoTotal.toString();
  }

  public double getSaldo(int pos) {
    return aplicacoesFinanceiras[pos].getSaldo();
  }

  public double getSaldo() {
    double total = 0;

    for (int pos = 0; pos < nRegAplicacoes; pos++) {
      total += getSaldo(pos);
    }

    return total;
  }

  public int getNumAgencia() {
    return numAgencia;
  }

  public void setNumAgencia(int numAge) {
    numAgencia = numAge;
  }

  public int getNumConta() {
    return numConta;
  }

  public void setNumConta(int numCo) {
    numConta = numCo;
  }

  public double getSaldoConta() {
    return saldoConta;
  }

  public void setSaldo(double saldoCon) {
    saldoConta = saldoCon;
  }

  public int arquivoSalvarDados(String pasta) {
    Path diretorio = Path.of(pasta);

    try {
      Files.createDirectories(diretorio);

      // Salva cada aplicação e seu histórico.
      for (int pos = 0; pos < nRegAplicacoes; pos++) {
        Path arquivo = diretorio.resolve(
            "aplicacao_" + pos + ".txt");

        int resultado = aplicacoesFinanceiras[pos]
            .arquivoSalvarDados(arquivo.toString());

        if (resultado < 0) {
          return -1;
        }
      }

      // Cada classe organiza seus dados; Arquivo apenas grava as linhas.
      List<String> linhas = Arrays.asList(
          "CLIENTE-1",
          cliente.getNome(),
          cliente.getCpf(),
          cliente.getCelular(),
          String.valueOf(numAgencia),
          String.valueOf(numConta),
          String.valueOf(saldoConta),
          String.valueOf(nRegAplicacoes));

      Arquivo.arquivoGravarDados(diretorio.resolve("cliente.txt").toString(), linhas);

      return nRegAplicacoes;
    } catch (IOException e) {
      return -1;
    }
  }

  public int arquivoLerDados(String pasta) {
    try {
      Path diretorio = Path.of(pasta);
      List<String> linhas = Arquivo.arquivoLerDados(
          diretorio.resolve("cliente.txt").toString());

      if (linhas.size() != 8 || !"CLIENTE-1".equals(linhas.get(0))) {
        return -1;
      }

      String nome = linhas.get(1);
      String cpf = linhas.get(2);
      String celular = linhas.get(3);
      int agenciaLida = Integer.parseInt(linhas.get(4));
      int contaLida = Integer.parseInt(linhas.get(5));
      double saldoLido = Double.parseDouble(linhas.get(6));
      int quantidade = Integer.parseInt(linhas.get(7));

      if (nome == null || cpf == null || celular == null || !Double.isFinite(saldoLido)
          || quantidade < 0 || quantidade > aplicacoesFinanceiras.length) {
        return -1;
      }

      DadosPessoais dadosLidos = new DadosPessoais();
      if (cpf.isEmpty()) {
        dadosLidos.setNome(nome);
        dadosLidos.setCelular(celular);
      } else {
        dadosLidos.cadastrarDados(nome, cpf, celular);
        if (!dadosLidos.getCpf().equals(cpf.replaceAll("\\D", ""))) {
          return -1;
        }
      }

      MultiOperacoesFinanceiras[] aplicacoesLidas =
          new MultiOperacoesFinanceiras[aplicacoesFinanceiras.length];
      AplicacaoFinanceira[] anteriores = new AplicacaoFinanceira[aplicacoesFinanceiras.length];
      AplicacaoFinanceira[] novas = new AplicacaoFinanceira[aplicacoesFinanceiras.length];

      for (int pos = 0; pos < aplicacoesLidas.length; pos++) {
        aplicacoesLidas[pos] = new MultiOperacoesFinanceiras();
        anteriores[pos] = aplicacoesFinanceiras[pos].getAplicacao();

        if (pos < quantidade) {
          String arquivo = diretorio.resolve("aplicacao_" + pos + ".txt").toString();
          if (aplicacoesLidas[pos].arquivoLerDados(arquivo, false) < 0) {
            return -1;
          }
        }
        novas[pos] = aplicacoesLidas[pos].getAplicacao();
      }

      // Confere os numeros de todas as aplicacoes antes de substituir o cadastro.
      AplicacaoFinanceira.registrarDadosLidos(anteriores, novas);
      cliente = dadosLidos;
      numAgencia = agenciaLida;
      numConta = contaLida;
      saldoConta = saldoLido;
      nRegAplicacoes = quantidade;
      aplicacoesFinanceiras = aplicacoesLidas;
      return quantidade;
    } catch (IOException | IllegalArgumentException | NullPointerException e) {
      return -1;
    }
  }

}
