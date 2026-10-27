# Aplicação financeira

Exercício em Java, sem bibliotecas externas. A classe `AplicacaoFinanceira`
pode ser herdada e mantém os nomes dos atributos e métodos do código original.
O `Main` mostra um cadastro, a passagem de 30 dias, os saldos e um resgate.

## Executar

Na pasta do projeto:

```powershell
javac -encoding UTF-8 -d bin src/AplicacaoFinanceira.java src/Main.java
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
  a representar a parte que permanece aplicada. O valor aplicado conserva
  o aporte inicial.
- Todos os atributos têm getters e setters. Os setters das taxas mantêm a
  conversão automática. Para efetuar uma retirada, use `resgateValor`;
  `setVALORRESGATADO` apenas altera o total armazenado. Depois de alterar
  dias ou parâmetros de cálculo, chame `atualizarSaldo()`.

No exemplo, R$ 1.000,00 a 1% ao mês geram R$ 10,00 brutos em 30 dias,
R$ 2,25 de imposto e saldo líquido de R$ 1.007,75. Após resgatar R$ 100,00,
restam R$ 907,75.
