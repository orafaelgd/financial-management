public class Main {
        public static void main(String[] args) {
                Clientes cadastro = new Clientes();

                cadastro.setCadastroCliente(
                                "Cliente de exemplo", "52998224725", "11999999999",
                                1, 1001, 2000.0);

                // Primeiro cliente, na posição 0, ainda sem aplicações.
                System.out.println(cadastro.listarAplicacoes(0));

                cadastro.setCadastroAplicacao(
                                0, // Posição do cliente
                                10, 11, 2026, // Data de cadastro
                                10, 11, 2026, // Data da operação
                                "Aplicação de exemplo", "APP-001",
                                1000.0, // Valor aplicado
                                0.0, 1.0, // Taxa diária e mensal
                                1, // Rendimento mensal
                                365, 0, // Prazo e permanência mínima
                                0, 0, 0, // Tipo de resgate, IOF e imposto
                                "Investimento inicial");

                int resultado = cadastro.realizarResgate(
                                0, 0, 10, 11, 2026, 100.0);

                System.out.println(
                                resultado == 1
                                                ? "Resgate realizado."
                                                : "Resgate não permitido.");

                System.out.println("\nAPLICAÇÕES CADASTRADAS");
                System.out.println(cadastro.listarAplicacoes(0));

                System.out.println("DETALHES DA APLICAÇÃO");
                System.out.println(cadastro.getDetalheAplicacao(0, 0));

                System.out.printf(
                                "Saldo da conta: R$ %.2f%n",
                                cadastro.getSaldoConta(0));

                System.out.printf(
                                "Total nas aplicações: R$ %.2f%n",
                                cadastro.getSaldoTotalAplicacoes(0));

                // A aplicação tem R$ 900; tentar retirar R$ 1.000 deve falhar.
                int tentativa = cadastro.realizarResgate(
                                0, 0, 10, 11, 2026, 1000.0);

                if (tentativa == 1) {
                        System.out.println("Resgate realizado.");
                } else {
                        String motivo = cadastro.getMotivoRecusaResgate(
                                        0, 0, 1000.0);

                        System.out.println("Resgate não permitido: " + motivo);
                }

                System.out.printf(
                                "Saldo da conta: R$ %.2f%n",
                                cadastro.getSaldoConta(0));

                System.out.printf(
                                "Total nas aplicações: R$ %.2f%n",
                                cadastro.getSaldoTotalAplicacoes(0));

                System.out.println("\nEXTRATO");

                System.out.println(
                                cadastro.getExtratoData(
                                                0,
                                                java.time.LocalDate.of(2026, 11, 10),
                                                java.time.LocalDate.of(2026, 11, 10)));

        }
}