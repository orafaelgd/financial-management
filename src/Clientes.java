import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Clientes {
  private ContaFinanceira[] clientes = new ContaFinanceira[100];
  private int nRegistroClientes;

  Clientes() {
    for (int i = 0; i < 100; i++) {
      clientes[i] = new ContaFinanceira();
    }
    nRegistroClientes = 0;
  }

  public int getNRegistro() {
    return nRegistroClientes;
  }

  public void setNumAgencia(int numA, int pos) {
    clientes[pos].setNumAgencia(numA);
  }

  public void setNumConta(int numCon, int pos) {
    clientes[pos].setNumConta(numCon);
  }

  public void setSaldoConta(double sald, int pos) {
    clientes[pos].setSaldo(sald);
  }

  public int getNumAgencia(int pos) {
    return clientes[pos].getNumAgencia();
  }

  public int getNumConta(int pos) {
    return clientes[pos].getNumConta();
  }

  public double getSaldoConta(int pos) {
    return clientes[pos].getSaldoConta();
  }

  public String listarClientes() {
    return toString();
  }

  public int setCadastroCliente(String nom, String cpf, String celu, int numeroAgencia, int numeroConta,
      double saldo) {
    if (nRegistroClientes >= clientes.length) {
      return 0;
    }
    try {
      clientes[nRegistroClientes].setCadastroCliente(nom, cpf, celu, numeroAgencia, numeroConta, saldo);
      nRegistroClientes++;
      return 1;
    } catch (IllegalArgumentException e) {
      System.out.println("\n" + e + "\n");
      return 0;
    }
  }

  public void setCadastroAplicacao(int posCliente, int dia, int mes, int ano,
      int diaOpera, int mesOpera, int anoOpera,
      String nomeAplicacao, String numAplicacao,
      double valorAplicacao, double taxaDiaria, double taxaMes,
      int tipoDeRenda, int tempoFinalInvestimento, int tempoMinimoPermanencia,
      int tipoResgate, int Iof, int tipoImposto, String descricao) {

    clientes[posCliente].setCadastroAplicacao(dia, mes, ano, diaOpera, mesOpera, anoOpera, nomeAplicacao,
        numAplicacao, valorAplicacao, taxaDiaria, taxaMes, tipoDeRenda, tempoFinalInvestimento, tempoMinimoPermanencia,
        tipoResgate, Iof, tipoImposto, descricao);

  }

  public String getExtratoTotal(int pos) {
    return getExtrato(pos);
  }

  public String getExtratoAplicacao(int posCliente, int posAplicacao) {
    return getExtrato(posCliente, posAplicacao);
  }

  public String getExtratoData(int posCliente, LocalDate ini, LocalDate fi) {
    return getExtrato(posCliente, ini, fi);
  }

  public double getSaldoTotalAplicacoes(int posCliente) {
    return getSaldo(posCliente);
  }

  public double getSaldoAplicacao(int posCliente, int posAplicacao) {
    return getSaldo(posCliente, posAplicacao);
  }

  public String listarAplicacoes(int posCliente) {
    return clientes[posCliente].listarAplicacoes();
  }

  public String getDetalheAplicacao(int posCliente, int posAplicacao) {
    return clientes[posCliente].getDetalhesAplicacao(posAplicacao);
  }

  public void realizarAporte(int posCliente, int posAplicacao, int dia, int mes, int ano, double valor) {

    if (posCliente < 0 || posCliente >= nRegistroClientes) {
      throw new IllegalArgumentException("Cliente não cadastrado.");
    }

    clientes[posCliente].realizarAporte(posAplicacao, dia, mes, ano, valor);
  }

  public int realizarResgate(int posCliente, int posAplicacao, int d, int m, int an, double vl) {
    return clientes[posCliente].realizarResgate(posAplicacao, d, m, an, vl);
  }

  public String getMotivoRecusaResgate(int posCliente, int posAplicacao, double vl) {
    return clientes[posCliente].getMotivoRecusaResgate(posAplicacao, vl);
  }

  public int arquivoSalvarDados(int posCliente, String pasta) {
    return clientes[posCliente].arquivoSalvarDados(pasta);
  }

  public int arquivoLerDados(int posCliente, String pasta) {
    if (posCliente < 0
        || posCliente >= clientes.length
        || posCliente > nRegistroClientes) {

      throw new IllegalArgumentException(
          "Informe uma posição cadastrada ou a próxima disponível.");
    }

    int resultado = clientes[posCliente].arquivoLerDados(pasta);

    if (resultado >= 0 && posCliente == nRegistroClientes) {
      nRegistroClientes++;
    }

    return resultado;
  }

  public void setCadastro(String nome, String cpf, String celular,
      int agencia, int conta, double saldo) {
    setCadastroCliente(nome, cpf, celular, agencia, conta, saldo);
  }

  // A cópia protege a organização do array; as contas continuam acessíveis pelos
  // métodos.
  public ContaFinanceira[] getClientes() {
    return Arrays.copyOf(clientes, nRegistroClientes);
  }

  public int getNRegistroClientes() {
    return nRegistroClientes;
  }

  public void setCliente(int posCliente, ContaFinanceira conta) {
    if (posCliente < 0 || posCliente > nRegistroClientes || posCliente >= clientes.length) {
      throw new IllegalArgumentException("Posição de cliente inválida.");
    }
    ContaFinanceira[] contas = Arrays.copyOf(clientes,
        Math.max(nRegistroClientes, posCliente + 1));
    contas[posCliente] = conta;
    setClientes(contas);
  }

  public void setClientes(ContaFinanceira[] contas) {
    if (contas == null || contas.length > clientes.length) {
      throw new IllegalArgumentException("Quantidade de clientes inválida.");
    }
    Set<ContaFinanceira> unicas = new HashSet<>();
    ContaFinanceira[] novos = new ContaFinanceira[clientes.length];
    for (int pos = 0; pos < contas.length; pos++) {
      ContaFinanceira conta = contas[pos];
      if (conta == null || conta.getCliente().getCpf().isEmpty() || !unicas.add(conta)) {
        throw new IllegalArgumentException("Informe contas cadastradas, sem repetições.");
      }
      novos[pos] = conta;
    }
    for (int pos = contas.length; pos < novos.length; pos++) {
      novos[pos] = new ContaFinanceira();
    }
    AplicacaoFinanceira.registrarDadosLidos(
        aplicacoesDasContas(clientes, nRegistroClientes), aplicacoesDasContas(novos, contas.length));
    clientes = novos;
    nRegistroClientes = contas.length;
  }

  // O contador pode reduzir o cadastro; novos clientes entram por
  // setCadastro/setCliente.
  public void setNRegistroClientes(int quantidade) {
    if (quantidade < 0 || quantidade > nRegistroClientes) {
      throw new IllegalArgumentException("O contador deve corresponder a clientes já cadastrados.");
    }
    if (quantidade != nRegistroClientes) {
      setClientes(Arrays.copyOf(clientes, quantidade));
    }
  }

  public void excluirCliente(int posCliente) {
    if (posCliente < 0 || posCliente >= nRegistroClientes) {
      throw new IllegalArgumentException("Cliente não cadastrado.");
    }
    ContaFinanceira[] contas = new ContaFinanceira[nRegistroClientes - 1];
    int pos = 0;
    for (int i = 0; i < nRegistroClientes; i++) {
      if (i != posCliente) {
        contas[pos] = clientes[i];
        pos++;
      }
    }
    setClientes(contas);
  }

  public String getNome(int posCliente) {
    return clientes[posCliente].getCliente().getNome();
  }

  public void setNome(int posCliente, String nome) {
    clientes[posCliente].getCliente().setNome(nome);
  }

  public String getCpf(int posCliente) {
    return clientes[posCliente].getCliente().getCpf();
  }

  public void setCpf(int posCliente, String cpf) {
    clientes[posCliente].getCliente().setCpf(cpf);
  }

  public String getCelular(int posCliente) {
    return clientes[posCliente].getCliente().getCelular();
  }

  public void setCelular(int posCliente, String celular) {
    clientes[posCliente].getCliente().setCelular(celular);
  }

  public void atualizarAplicacao(int dia, int mes, int ano) {
    LocalDate data = LocalDate.of(ano, mes, dia);
    if (data.getDayOfWeek().getValue() > 5) {
      throw new IllegalArgumentException("A atualização deve ocorrer de segunda a sexta-feira.");
    }
    // Confere todas as contas antes de começar, evitando atualizar só parte do
    // cadastro.
    for (int pos = 0; pos < nRegistroClientes; pos++) {
      clientes[pos].validarAtualizacao(data);
    }
    for (int pos = 0; pos < nRegistroClientes; pos++) {
      clientes[pos].atualizarAplicacao(dia, mes, ano);
    }
  }

  public void atualizarAplicacao(int posCliente, int dia, int mes, int ano) {
    clientes[posCliente].atualizarAplicacao(dia, mes, ano);
  }

  public double getSaldo() {
    double total = 0;
    for (int pos = 0; pos < nRegistroClientes; pos++) {
      total += getSaldo(pos);
    }
    return total;
  }

  public double getSaldo(int posCliente) {
    return clientes[posCliente].getSaldoTotalAplicacoes();
  }

  public double getSaldo(int posCliente, int posAplicacao) {
    return clientes[posCliente].getSaldoAplicacao(posAplicacao);
  }

  public double getSaldoTotalAplicacoes() {
    return getSaldo();
  }

  public String getExtrato() {
    LocalDate fim = LocalDate.now();
    return getExtrato(fim.minusDays(29), fim);
  }

  public String getExtrato(int posCliente) {
    return clientes[posCliente].getExtrato();
  }

  public String getExtrato(int posCliente, int posAplicacao) {
    return clientes[posCliente].getExtrato(posAplicacao);
  }

  public String getExtrato(int posCliente, LocalDate inicio, LocalDate fim) {
    validarPeriodo(inicio, fim);
    return clientes[posCliente].getExtrato(inicio, fim);
  }

  public String getExtrato(int posCliente, int posAplicacao, LocalDate inicio, LocalDate fim) {
    validarPeriodo(inicio, fim);
    return clientes[posCliente].getExtrato(posAplicacao, inicio, fim);
  }

  public String getExtrato(LocalDate inicio, LocalDate fim) {
    validarPeriodo(inicio, fim);
    if (nRegistroClientes == 0) {
      return "Nenhum cliente cadastrado.";
    }
    StringBuilder extrato = new StringBuilder();
    for (int pos = 0; pos < nRegistroClientes; pos++) {
      extrato.append("Cliente: ").append(getNome(pos))
          .append(" | Agência: ").append(getNumAgencia(pos))
          .append(" | Conta: ").append(getNumConta(pos)).append("\n")
          .append(getExtrato(pos, inicio, fim)).append("\n");
    }
    return extrato.toString();
  }

  private void validarPeriodo(LocalDate inicio, LocalDate fim) {
    if (inicio == null || fim == null || inicio.isAfter(fim)) {
      throw new IllegalArgumentException("Período inválido.");
    }
  }

  private AplicacaoFinanceira[] aplicacoesDasContas(ContaFinanceira[] contas, int quantidade) {
    List<AplicacaoFinanceira> aplicacoes = new ArrayList<>();
    for (int pos = 0; pos < quantidade; pos++) {
      aplicacoes.addAll(Arrays.asList(contas[pos].getAplicacoesRegistradas()));
    }
    return aplicacoes.toArray(new AplicacaoFinanceira[0]);
  }

  @Override
  public String toString() {
    if (nRegistroClientes == 0) {
      return "Nenhum cliente cadastrado.";
    }
    String cli = "";
    for (int i = 0; i < nRegistroClientes; i++) {
      cli += String.format("| %s || %s || %s || %s || %s || %s || %s |%n", i, getNome(i), getCpf(i), getCelular(i),
          getNumAgencia(i), getNumConta(i), getSaldoConta(i));
    }

    return cli;
  }
}
