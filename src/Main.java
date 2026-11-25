import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.LocalDate;

public class Main {
        public static void main(String[] args) {
                Clientes cadastro = new Clientes();
                String op = " ";
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("\nBEM-VINDO AO SISTEMA DE GESTÃO DE INVESTIMENTO!!!\n");

                do {
                        try {

                                System.out.println("1 - Cadastrar Cliente");
                                System.out.println("2 - Cadastrar Aplicação Financeira para um Cliente");
                                System.out.println("3 - Consultar Dados de um Cliente específico");
                                System.out.print("\n");
                                System.out.println("4 - Listar Clientes");
                                System.out.println("5 - Listar Clientes e suas Aplicações");
                                System.out.println("6 - Saldo de um Cliente específico");
                                System.out.print("\n");
                                System.out.println(
                                                "7 - Cadastrar lançamentos de operações financeiras para uma Aplicação Financeira específica");
                                System.out.println("8 - Atualizar os valores de todas as operações financeiras");
                                System.out.print("\n");
                                System.out.println("9 - Consultar aplicações de um cliente específico");
                                System.out.println("10 - Alterar Dados de um Cliente");
                                System.out.println("11 - Excluir Cliente");
                                System.out.print("\n");
                                System.out.println("R - Ler os dados de um Cliente do Arquivo");
                                System.out.println("W - Armazenar os dados de um Cliente em Arquivo");
                                System.out.println("0 - Sair do sistema...");

                                System.out.print("\nDIGITE O QUE DESEJA FAZER: ");
                                op = teclado.readLine();
                                if (op == null) {
                                        break;
                                }
                                op = op.trim().toUpperCase();

                                switch (op) {
                                        case "1":
                                                cadastrarCliente(cadastro, teclado);
                                                break;
                                        case "2":
                                                cadastrarAplicacao(cadastro, teclado);
                                                break;
                                        case "3":
                                                consultarCliente(cadastro, teclado);
                                                break;
                                        case "4":
                                                listarClientes(cadastro);
                                                break;
                                        case "5":
                                                listarClientesAplicacoes(cadastro);
                                                break;
                                        case "6":
                                                consultarSaldo(cadastro, teclado);
                                                break;
                                        case "7":
                                                cadastrarOperacao(cadastro, teclado);
                                                break;
                                        case "8":
                                                atualizarAplicacoes(cadastro, teclado);
                                                break;
                                        case "9":
                                                consultarAplicacoes(cadastro, teclado);
                                                break;
                                        case "10":
                                                alterarCliente(cadastro, teclado);
                                                break;
                                        case "11":
                                                excluirCliente(cadastro, teclado);
                                                break;
                                        case "R":
                                                lerDados(cadastro, teclado);
                                                break;
                                        case "W":
                                                salvarDados(cadastro, teclado);
                                                break;
                                        case "0":
                                                System.out.println("Sistema encerrado.");
                                                break;
                                        default:
                                                System.out.println("Opção inválida.");
                                                break;
                                }

                        } catch (IOException e) {
                                System.out.println("Erro de leitura: " + e.getMessage());
                                break;
                        } catch (NumberFormatException e) {
                                System.out.println("Digite um número válido. Use ponto para os centavos.");
                        } catch (DateTimeException e) {
                                System.out.println("Data inválida.");
                        } catch (IllegalArgumentException | IllegalStateException e) {
                                System.out.println("Erro: " + e.getMessage());
                        }

                } while (!"0".equals(op));

        }

        public static void cadastrarCliente(Clientes cadastro, BufferedReader teclado) {
                String nome, cpf, celular;
                int numeroAgencia, numeroConta;
                double valor;

                try {
                        System.out.print("Digite o Nome do seu Cliente: ");
                        nome = teclado.readLine();

                        System.out.print("Digite o CPF do seu Cliente: ");
                        cpf = teclado.readLine();

                        System.out.print("Digite o Celular do seu Cliente: ");
                        celular = teclado.readLine();

                        System.out.print("Digite o Número da Agência: ");
                        numeroAgencia = Integer.parseInt(teclado.readLine());

                        System.out.print("Digite o Número da Conta: ");
                        numeroConta = Integer.parseInt(teclado.readLine());

                        System.out.print("Digite o Saldo do seu Cliente: ");
                        valor = Double.parseDouble(teclado.readLine());

                        int resultado = cadastro.setCadastroCliente(nome, cpf, celular, numeroAgencia, numeroConta,
                                        valor);

                        if (resultado == 1) {
                                System.out.println("\nCliente Cadastrado com sucesso!!\n");
                        } else {
                                System.out.println("Não foi possível realizar o Cadastro...");
                        }

                } catch (IOException e) {
                        System.out.println(String.format("ERRO ENCONTRADO: %s", e));
                } catch (IllegalStateException x) {
                        System.out.println("ERRO AO CADASTRAR CLIENTE: " + x.getMessage());
                }

        }

        public static void cadastrarAplicacao(Clientes ob, BufferedReader teclado) throws IOException {
                int posCliente = escolherCliente(ob, teclado);
                LocalDate data = lerData(teclado);

                System.out.print("Nome da Aplicação: ");
                String nome = teclado.readLine();
                System.out.print("Número da Aplicação: ");
                String numero = teclado.readLine();
                System.out.print("Valor da Aplicação: ");
                double valor = Double.parseDouble(teclado.readLine());
                System.out.print("Taxa Diária em % (0 se usar a mensal): ");
                double taxaDiaria = Double.parseDouble(teclado.readLine());
                System.out.print("Taxa Mensal em % (0 se usar a diária): ");
                double taxaMes = Double.parseDouble(teclado.readLine());
                System.out.print("Rendimento (0-Diário, 1-Mensal, 2-Trimestral, 3-Semestral, 4-Anual, 5-No vencimento): ");
                int tipoRenda = Integer.parseInt(teclado.readLine());
                System.out.print("Prazo Final em dias de rendimento: ");
                int prazo = Integer.parseInt(teclado.readLine());
                System.out.print("Permanência Mínima em dias corridos: ");
                int permanencia = Integer.parseInt(teclado.readLine());
                System.out.print("Resgate (0-Livre, 1-Mensal, 2-Trimestral, 3-Semestral, 4-Anual): ");
                int tipoResgate = Integer.parseInt(teclado.readLine());
                System.out.print("IOF (0-Isento, 1-Até 29 dias, 2-Sempre 3,5%): ");
                int iof = Integer.parseInt(teclado.readLine());
                System.out.print("Imposto de Renda (0-Isento, 1-Regressivo): ");
                int imposto = Integer.parseInt(teclado.readLine());
                System.out.print("Descrição da Operação: ");
                String descricao = teclado.readLine();

                ob.setCadastroAplicacao(posCliente, data.getDayOfMonth(), data.getMonthValue(), data.getYear(),
                                data.getDayOfMonth(), data.getMonthValue(), data.getYear(), nome, numero,
                                valor, taxaDiaria, taxaMes, tipoRenda, prazo, permanencia, tipoResgate, iof,
                                imposto, descricao);
                System.out.println("Aplicação Cadastrada com sucesso!");
        }

        public static void listarClientes(Clientes ob) {
                System.out.println("\nLISTA DE CLIENTES CADASTRADOS!!\n");
                System.out.println(ob.toString());
        }

        public static int escolherCliente(Clientes ob, BufferedReader teclado) throws IOException {
                if (ob.getNRegistro() == 0) {
                        throw new IllegalStateException("Nenhum cliente cadastrado.");
                }
                listarClientes(ob);
                System.out.print("Digite a Posição do Cliente: ");
                int pos = Integer.parseInt(teclado.readLine());
                if (pos < 0 || pos >= ob.getNRegistro()) {
                        throw new IllegalArgumentException("Cliente não cadastrado.");
                }
                return pos;
        }

        public static LocalDate lerData(BufferedReader teclado) throws IOException {
                System.out.print("Dia: ");
                int dia = Integer.parseInt(teclado.readLine());
                System.out.print("Mês: ");
                int mes = Integer.parseInt(teclado.readLine());
                System.out.print("Ano: ");
                int ano = Integer.parseInt(teclado.readLine());
                return LocalDate.of(ano, mes, dia);
        }

        public static void consultarCliente(Clientes ob, BufferedReader teclado) throws IOException {
                int pos = escolherCliente(ob, teclado);
                System.out.println("Nome: " + ob.getNome(pos));
                System.out.println("CPF: " + ob.getCpf(pos));
                System.out.println("Celular: " + ob.getCelular(pos));
                System.out.println("Agência: " + ob.getNumAgencia(pos));
                System.out.println("Conta: " + ob.getNumConta(pos));
                System.out.printf("Saldo da Conta: R$ %.2f%n", ob.getSaldoConta(pos));
        }

        public static void listarClientesAplicacoes(Clientes ob) {
                if (ob.getNRegistro() == 0) {
                        System.out.println("Nenhum cliente cadastrado.");
                }
                for (int i = 0; i < ob.getNRegistro(); i++) {
                        System.out.println("\nCliente: " + ob.getNome(i) + " | Posição: " + i);
                        System.out.println(ob.listarAplicacoes(i));
                }
        }

        public static void consultarSaldo(Clientes ob, BufferedReader teclado) throws IOException {
                int pos = escolherCliente(ob, teclado);
                System.out.printf("Saldo da Conta: R$ %.2f%n", ob.getSaldoConta(pos));
                System.out.printf("Saldo nas Aplicações: R$ %.2f%n", ob.getSaldoTotalAplicacoes(pos));
        }

        public static void cadastrarOperacao(Clientes ob, BufferedReader teclado) throws IOException {
                int posCliente = escolherCliente(ob, teclado);
                System.out.println(ob.listarAplicacoes(posCliente));
                System.out.print("Digite a Posição da Aplicação: ");
                int posAplicacao = Integer.parseInt(teclado.readLine());
                System.out.println(ob.getDetalheAplicacao(posCliente, posAplicacao));
                System.out.print("Operação (1-Aporte, 2-Resgate, 0-Voltar): ");
                String tipo = teclado.readLine();
                if ("0".equals(tipo)) {
                        return;
                }
                if (!"1".equals(tipo) && !"2".equals(tipo)) {
                        System.out.println("Operação inválida.");
                        return;
                }
                LocalDate data = lerData(teclado);
                System.out.print("Valor da Operação: ");
                double valor = Double.parseDouble(teclado.readLine());

                switch (tipo) {
                        case "1":
                                ob.realizarAporte(posCliente, posAplicacao, data.getDayOfMonth(),
                                                data.getMonthValue(), data.getYear(), valor);
                                System.out.println("Aporte realizado com sucesso!");
                                break;
                        case "2":
                                int resultado = ob.realizarResgate(posCliente, posAplicacao, data.getDayOfMonth(),
                                                data.getMonthValue(), data.getYear(), valor);
                                if (resultado == 1) {
                                        System.out.println("Resgate realizado com sucesso!");
                                } else {
                                        System.out.println("Resgate recusado: "
                                                        + ob.getMotivoRecusaResgate(posCliente, posAplicacao, valor));
                                }
                                break;
                }
        }

        public static void atualizarAplicacoes(Clientes ob, BufferedReader teclado) throws IOException {
                if (ob.getNRegistro() == 0) {
                        System.out.println("Nenhum cliente cadastrado.");
                        return;
                }
                System.out.println("Informe a Data de Atualização (segunda a sexta-feira):");
                LocalDate data = lerData(teclado);
                ob.atualizarAplicacao(data.getDayOfMonth(), data.getMonthValue(), data.getYear());
                System.out.println("Valores atualizados com sucesso!");
        }

        public static void consultarAplicacoes(Clientes ob, BufferedReader teclado) throws IOException {
                int posCliente = escolherCliente(ob, teclado);
                System.out.println(ob.listarAplicacoes(posCliente));
                if (ob.getClientes()[posCliente].getNRegAplicacoes() == 0) {
                        return;
                }
                System.out.print("Posição da Aplicação para ver detalhes (-1 para voltar): ");
                int posAplicacao = Integer.parseInt(teclado.readLine());
                if (posAplicacao == -1) {
                        return;
                }
                System.out.println(ob.getDetalheAplicacao(posCliente, posAplicacao));
                System.out.println("EXTRATO DOS ÚLTIMOS 30 DIAS:");
                System.out.println(ob.getExtratoAplicacao(posCliente, posAplicacao));
        }

        public static void alterarCliente(Clientes ob, BufferedReader teclado) throws IOException {
                int pos = escolherCliente(ob, teclado);
                System.out.print("Alterar (1-Nome, 2-CPF, 3-Celular, 4-Agência, 5-Conta, 0-Voltar): ");
                String campo = teclado.readLine();
                if ("0".equals(campo)) {
                        return;
                }
                System.out.print("Digite o Novo Valor: ");
                String valor = teclado.readLine();
                switch (campo) {
                        case "1":
                                ob.setNome(pos, valor);
                                break;
                        case "2":
                                ob.setCpf(pos, valor);
                                break;
                        case "3":
                                ob.setCelular(pos, valor);
                                break;
                        case "4":
                                ob.setNumAgencia(Integer.parseInt(valor), pos);
                                break;
                        case "5":
                                ob.setNumConta(Integer.parseInt(valor), pos);
                                break;
                        default:
                                System.out.println("Opção inválida.");
                                return;
                }
                System.out.println("Dados alterados com sucesso!");
        }

        public static void excluirCliente(Clientes ob, BufferedReader teclado) throws IOException {
                int pos = escolherCliente(ob, teclado);
                System.out.print("Excluir " + ob.getNome(pos) + " e suas aplicações? (S/N): ");
                if ("S".equalsIgnoreCase(teclado.readLine())) {
                        ob.excluirCliente(pos);
                        System.out.println("Cliente excluído com sucesso!");
                }
        }

        public static void salvarDados(Clientes ob, BufferedReader teclado) throws IOException {
                int pos = escolherCliente(ob, teclado);
                System.out.print("Pasta para salvar o Cliente e suas Aplicações: ");
                String pasta = teclado.readLine();
                int resultado = ob.arquivoSalvarDados(pos, pasta);
                if (resultado >= 0) {
                        System.out.println("Dados salvos com sucesso!");
                } else {
                        System.out.println("Não foi possível salvar os dados.");
                }
        }

        public static void lerDados(Clientes ob, BufferedReader teclado) throws IOException {
                listarClientes(ob);
                System.out.print("Posição para carregar (" + ob.getNRegistro() + " para um novo cliente): ");
                int pos = Integer.parseInt(teclado.readLine());
                System.out.print("Pasta com os dados do Cliente e suas Aplicações: ");
                String pasta = teclado.readLine();
                int resultado = ob.arquivoLerDados(pos, pasta);
                if (resultado >= 0) {
                        System.out.println("Cliente e suas Aplicações carregados com sucesso!");
                } else {
                        System.out.println("Não foi possível ler os dados. Confira a pasta e os arquivos.");
                }
        }
}
