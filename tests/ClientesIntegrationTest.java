import java.time.LocalDate;

// Executável sem bibliotecas externas; verifica as operações e consultas do cadastro.
public class ClientesIntegrationTest {
  private static final LocalDate INICIO = LocalDate.of(2026, 11, 10);

  public static void main(String[] args) throws Exception {
    testar();
    System.out.println("OK: cadastro, consultas, atualizacoes, movimentacoes e acessores.");
  }

  private static void testar() throws Exception {
    Clientes clientes = new Clientes();
    rejeitar(() -> clientes.setCadastro("Inválido", "11111111111", "1199", 1, 1, 100),
        "CPF inválido");
    igual(0, clientes.getNRegistroClientes(), "CPF inválido não consome posição");
    clientes.setCadastro("Ana", "529.982.247-25", "11999999999", 1, 101, 2000);
    clientes.setCadastroCliente("Bruno", "11144477735", "21999999999", 2, 202, 3000);
    igual("52998224725", clientes.getCpf(0), "normalização do CPF");
    rejeitar(() -> clientes.getCliente(2), "cliente inexistente");
    rejeitar(() -> clientes.getSaldo(0, 0), "aplicação inexistente");
    rejeitar(() -> clientes.getCliente(0).setCadastroCliente("Outro", "123", "99", 3, 303, 10),
        "cadastro de conta inválido");
    igual("Ana", clientes.getNome(0), "cadastro inválido preserva os dados da conta");

    aplicar(clientes, 0, "TEST-A", 1000);
    aplicar(clientes, 0, "TEST-B", 200);
    aplicar(clientes, 1, "TEST-C", 1500);
    perto(2700, clientes.getSaldo(), "saldo geral inicial");
    perto(1200, clientes.getSaldo(0), "saldo por cliente");
    perto(1000, clientes.getSaldo(0, 0), "saldo individual");
    clientes.realizarAporte(0, 0, 10, 11, 2026, 200);
    igual(1, clientes.realizarResgate(0, 0, 10, 11, 2026, 100), "resgate válido");
    igual(0, clientes.realizarResgate(0, 0, 10, 11, 2026, 5000), "resgate recusado");
    perto(700, clientes.getSaldoConta(0), "saldo da conta após aporte e resgate");
    perto(2800, clientes.getSaldo(), "saldo aplicado após movimentações");
    rejeitar(() -> clientes.realizarAporte(0, 99, 10, 11, 2026, 1), "índice do aporte");
    rejeitar(() -> clientes.realizarResgate(0, 99, 10, 11, 2026, 1), "índice do resgate");
    rejeitar(() -> clientes.realizarAporte(0, 0, 10, 11, 2026, Double.NaN), "aporte NaN");
    rejeitar(() -> clientes.realizarResgate(0, 0, 10, 11, 2026, Double.POSITIVE_INFINITY), "resgate infinito");
    contem(clientes.getMotivoRecusaResgate(0, 0, 5000), "supera o saldo", "motivo da recusa");

    String individual = clientes.getExtrato(0, 0, INICIO, INICIO);
    contem(individual, "Aporte", "histórico de aporte");
    contem(individual, "Resgate | -100.0", "histórico de resgate");
    verificar(!individual.contains("5000.0"), "recusa não gera lançamento");
    verificar(!individual.contains("TEST-B"), "extrato individual não mistura aplicações");
    contem(clientes.getExtrato(0, INICIO, INICIO), "TEST-B", "todas as aplicações do cliente");
    contem(clientes.getExtrato(INICIO, INICIO), "Bruno", "extrato geral inclui outro cliente");
    rejeitar(() -> clientes.getExtrato(INICIO.plusDays(1), INICIO), "período invertido");
    rejeitar(() -> clientes.getExtrato((LocalDate) null, INICIO), "período nulo");
    clientes.getExtrato();
    clientes.getExtrato(0);
    clientes.getExtrato(0, 0);

    String antes = clientes.getExtrato(INICIO, INICIO.plusYears(1));
    rejeitar(() -> clientes.atualizarAplicacao(14, 11, 2026), "atualização no sábado");
    igual(antes, clientes.getExtrato(INICIO, INICIO.plusYears(1)), "sábado não altera histórico");
    clientes.atualizarAplicacao(11, 11, 2026);
    clientes.atualizarAplicacao(1, 12, 11, 2026);
    antes = clientes.getExtrato(0, INICIO, INICIO.plusYears(1));
    rejeitar(() -> clientes.atualizarAplicacao(11, 11, 2026), "data anterior em uma das contas");
    igual(antes, clientes.getExtrato(0, INICIO, INICIO.plusYears(1)), "falha não atualiza só o primeiro cliente");
    clientes.atualizarAplicacao(22, 12, 2026); // 30 dias de segunda a sexta desde 10/11.
    perto(1111, clientes.getSaldo(0, 0), "rendimento da primeira aplicação");
    perto(202, clientes.getSaldo(0, 1), "rendimento da segunda aplicação");
    perto(1515, clientes.getSaldo(1, 0), "rendimento do segundo cliente");

    MultiOperacoesFinanceiras historico = clientes.getCliente(1).aplicacoesFinanceiras[0];
    int quantidade = historico.getQtdOperacoes();
    historico.setQtdOperacoes(100);
    antes = clientes.getExtrato(0, INICIO, INICIO.plusYears(1));
    rejeitar(() -> clientes.atualizarAplicacao(23, 12, 2026), "histórico cheio em uma conta");
    igual(antes, clientes.getExtrato(0, INICIO, INICIO.plusYears(1)), "histórico cheio evita atualização parcial");
    historico.setQtdOperacoes(quantidade);

    clientes.setNome(0, "Ana atualizada");
    clientes.setCelular(0, "11888888888");
    clientes.setCpf(0, "529.982.247-25");
    igual("11888888888", clientes.getCelular(0), "acessor do celular");
    ContaFinanceira[] copia = clientes.getClientes();
    copia[0] = null;
    igual("Ana atualizada", clientes.getNome(0), "alterar cópia não altera array interno");
    rejeitar(() -> clientes.setNRegistroClientes(3), "contador não inventa cliente");
    rejeitar(() -> clientes.setClientes(new ContaFinanceira[] { clientes.getCliente(0), clientes.getCliente(0) }),
        "array não aceita conta repetida");

    clientes.setCliente(0, clientes.getCliente(0));
    clientes.setNRegistroClientes(1);
    igual(1, clientes.getNRegistroClientes(), "redução coerente do contador");
    rejeitar(() -> clientes.getCliente(1), "cliente removido pelo contador");
    clientes.setClientes(new ContaFinanceira[0]);

    clientes.setCadastro("Extrato recente", "52998224725", "1199", 1, 1, 100);
    LocalDate hoje = LocalDate.now();
    aplicarNaData(clientes, "TEST-HOJE", hoje, "LANCAMENTO-HOJE");
    aplicarNaData(clientes, "TEST-LIMITE", hoje.minusDays(29), "LANCAMENTO-LIMITE");
    aplicarNaData(clientes, "TEST-ANTIGO", hoje.minusDays(30), "LANCAMENTO-ANTIGO");
    aplicarNaData(clientes, "TEST-FUTURO", hoje.plusDays(1), "LANCAMENTO-FUTURO");
    String ultimos30 = clientes.getExtrato();
    contem(ultimos30, "LANCAMENTO-HOJE", "últimos 30 dias incluem hoje");
    contem(ultimos30, "LANCAMENTO-LIMITE", "últimos 30 dias incluem o limite inicial");
    verificar(!ultimos30.contains("LANCAMENTO-ANTIGO"), "exclui lançamento de 30 dias atrás");
    verificar(!ultimos30.contains("LANCAMENTO-FUTURO"), "exclui lançamento futuro");
    contem(clientes.getExtrato(0), "LANCAMENTO-LIMITE", "últimos 30 dias por cliente");
    verificar(!clientes.getExtrato(0, 2).contains("LANCAMENTO-ANTIGO"), "últimos 30 dias por aplicação");
    contem(clientes.getExtrato(0, 2, hoje.minusDays(30), hoje.minusDays(30)),
        "LANCAMENTO-ANTIGO", "período explícito recupera lançamento antigo");
    clientes.setClientes(new ContaFinanceira[0]);
  }

  private static void aplicar(Clientes clientes, int cliente, String numero, double valor) {
    clientes.setCadastroAplicacao(cliente, 10, 11, 2026, 10, 11, 2026,
        "Aplicação " + numero, numero, valor, 0, 1, 1, 365, 0, 0, 0, 0, "Investimento inicial");
  }

  private static void aplicarNaData(Clientes clientes, String numero, LocalDate data, String descricao) {
    clientes.setCadastroAplicacao(0, data.getDayOfMonth(), data.getMonthValue(), data.getYear(),
        data.getDayOfMonth(), data.getMonthValue(), data.getYear(), numero, numero,
        1, 0, 1, 1, 365, 0, 0, 0, 0, descricao);
  }

  private static void verificar(boolean condicao, String contexto) {
    if (!condicao) {
      throw new AssertionError(contexto);
    }
  }

  private static void igual(Object esperado, Object obtido, String contexto) {
    verificar(esperado.equals(obtido), contexto + ": esperado " + esperado + ", obtido " + obtido);
  }

  private static void perto(double esperado, double obtido, String contexto) {
    verificar(Double.isFinite(obtido) && Math.abs(esperado - obtido) < 0.000001,
        contexto + ": esperado " + esperado + ", obtido " + obtido);
  }

  private static void contem(String texto, String trecho, String contexto) {
    verificar(texto.contains(trecho), contexto + ": " + texto);
  }

  private static void rejeitar(Runnable operacao, String contexto) {
    try {
      operacao.run();
    } catch (IllegalArgumentException | IllegalStateException e) {
      return;
    }
    throw new AssertionError("Deveria recusar: " + contexto);
  }
}
