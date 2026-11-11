# Aplicação financeira

Exercício em Java, sem bibliotecas externas. `Clientes` gerencia contas,
aplicações e históricos por delegação a `ContaFinanceira`,
`MultiOperacoesFinanceiras` e `AplicacaoFinanceira`.
O `Main` demonstra cadastro, consulta, resgate autorizado, recusa e extrato.

## Executar

Na pasta do projeto:

```powershell
javac -encoding UTF-8 -d bin src/*.java
java -cp bin Main
```

## Uso e cálculos

- `setCadastro(...)` inicia a aplicação no dia zero e verifica se o número
  já está em uso por outra aplicação nesta execução do programa.
- As taxas são percentuais: `1` significa 1%. Foi mantida a conversão simples
  do código inicial: taxa diária = mensal / 30; mensal = diária * 30.
  No cadastro, informe uma taxa e use zero na outra; a mensal tem prioridade
  quando ambas são positivas.
- `incrementarDia()` conta os dias corridos, usados nos impostos e resgates.
  `incrementarDiaAplicacao()` conta os dias de rendimento, até o vencimento.
  Os dois contadores são independentes; o exemplo avança ambos.
- `atualizarSaldo()` credita somente períodos completos: 1, 30, 90, 180 ou
  365 dias. As taxas trimestral, semestral e anual são, respectivamente,
  3, 6 e 12 vezes a mensal, como no código inicial. Cada período rende sobre
  o saldo bruto. No tipo 5, o rendimento diário composto é creditado apenas
  ao completar o prazo final em dias efetivos. Dias já calculados não rendem novamente.
- IOF e IR seguem os valores do enunciado. O IOF tipo 1 deixa de ser cobrado
  a partir do dia 30; o tipo 2 cobra sempre 3,5% do rendimento. O IR incide
  sobre o rendimento após IOF: 22,5% até 180 dias, 20% até 360, 17,5% até 720
  e 15% a partir de 721. O imposto devido é recalculado sobre o rendimento
  ainda aplicado, conforme o prazo atual.
- `resgateValor(valor)` recebe um valor líquido e retorna `1` para sucesso
  ou `0` para erro. Respeita a permanência mínima e os dias de resgate
  contados desde o cadastro. No vencimento e depois dele, libera o resgate
  independentemente da periodicidade. Permite retirar todo o saldo.
  O total resgatado é acumulado; saldos, rendimentos e imposto devido passam
  a representar a parte que permanece aplicada. O valor aplicado acumula
  o investimento inicial e os aportes, sem diminuir nos resgates.
- Todos os atributos têm getters e setters. Os setters das taxas mantêm a
  conversão automática. Para efetuar uma retirada, use `resgateValor`;
  `setVALORRESGATADO` apenas altera o total armazenado. Depois de alterar
  dias ou parâmetros de cálculo, chame `atualizarSaldo()`.

No exemplo, R$ 1.000,00 a 1% ao mês geram R$ 10,00 brutos em 30 dias,
R$ 2,25 de imposto e saldo líquido de R$ 1.007,75. Após resgatar R$ 100,00,
restam R$ 907,75.

## Gerenciamento de múltiplos clientes (item 7)

O construtor e a estrutura com 100 posições foram mantidos. Cadastros inválidos
lançam uma exceção antes de alterar os dados ou incrementar o contador.

```java
Clientes cadastro = new Clientes();
cadastro.setCadastro("Cliente de exemplo", "52998224725", "11999999999", 1, 1001, 2000);

// Todos os clientes, uma conta ou uma aplicação. Os valores são saldos líquidos investidos.
double geral = cadastro.getSaldo();
double porCliente = cadastro.getSaldo(0);
// double individual = cadastro.getSaldo(0, 0); // Após cadastrar a aplicação.

// Todas as aplicações de todos os clientes; somente de segunda a sexta-feira.
cadastro.atualizarAplicacao(11, 11, 2026);
```

As sobrecargas de `getExtrato` permitem consultar:

- `getExtrato()`: todos os clientes, últimos 30 dias, incluindo hoje.
- `getExtrato(posCliente)`: todas as aplicações de uma conta, últimos 30 dias.
- `getExtrato(posCliente, posAplicacao)`: uma aplicação, últimos 30 dias.
- As três consultas também aceitam `LocalDate inicio, LocalDate fim` no final,
  com limites inclusivos. No extrato geral, os argumentos são somente as datas.

Os nomes anteriores (`getExtratoTotal`, `getExtratoAplicacao`, `getExtratoData`,
`getSaldoTotalAplicacoes` e `getSaldoAplicacao`) continuam disponíveis.
A atualização coletiva valida a data e o espaço no histórico de todas as
aplicações antes de iniciar, evitando atualizar apenas parte do cadastro.
Uma consulta de saldo não avança automaticamente o calendário.

Há acessores para os dados pessoais, agência, conta e saldo da conta.
`getClientes()` retorna uma cópia do array de contas cadastradas, com referências
às contas; não é uma cópia profunda. `setClientes(...)` recebe somente as contas
cadastradas, sem posições vazias ou repetidas, e ajusta o contador. `setCliente`
substitui uma posição ou inclui na próxima disponível. `setNRegistroClientes`
permite reduzir o cadastro; aumentar a quantidade exige cadastrar as contas.

## Verificação

Com Java 11 ou superior, os testes de integração podem ser executados sem JUnit:

```powershell
$sgiTeste = Join-Path ([System.IO.Path]::GetTempPath()) ('sgi-test-' + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $sgiTeste | Out-Null
javac -encoding UTF-8 -d $sgiTeste src/*.java tests/ClientesIntegrationTest.java
java -cp $sgiTeste ClientesIntegrationTest
```

Os testes cobrem múltiplos clientes/aplicações, filtros de extrato, atualização
coletiva, fins de semana, CPF inválido, saldos, movimentações e acessores.
