public class Main {
    public static void main(String[] args) {
        AplicacaoFinanceira aplicacao = new AplicacaoFinanceira();

        // Nome, numero, valor, taxa diaria, taxa mensal,
        // tipo de rendimento, vencimento, permanencia minima, tipo de resgate,
        // IOF, tipo de imposto, dia, mes e ano do cadastro.
        aplicacao.setCadastro("Minha aplicacao", "001", 1000, 0, 1,
                1, 365, 30, 1, 1, 1, 27, 10, 2026);

        for (int dia = 0; dia < 30; dia++) {
            aplicacao.incrementarDia();
            aplicacao.incrementarDiaAplicacao();
        }
        aplicacao.atualizarSaldo();

        System.out.println("Aplicacao: " + aplicacao.getNOMEAPLICACAO());
        System.out.println("Numero: " + aplicacao.getNUMEROAPLICACAPFINANCEIRA());
        System.out.println("Dias corridos: " + aplicacao.getTEMPOATUALIZADOAPLICACAO());
        System.out.printf("Valor aplicado: R$ %.2f%n", aplicacao.getVALORAPLICADO());
        System.out.printf("Rendimento bruto: R$ %.2f%n", aplicacao.getRENDIMENTOATUALBRUTO());
        System.out.printf("Imposto devido: R$ %.2f%n", aplicacao.getIMPOSTOATUALDEVIDO());
        System.out.printf("Rendimento liquido: R$ %.2f%n", aplicacao.getRENDIMENTOATUALLIQUIDO());
        System.out.printf("Saldo bruto: R$ %.2f%n", aplicacao.getVALORATUALBRUTO());
        System.out.printf("Saldo liquido: R$ %.2f%n", aplicacao.getVALORATUALLIQUIDO());

        if (aplicacao.resgateValor(100) == 1) {
            System.out.printf("Total resgatado: R$ %.2f%n", aplicacao.getVALORRESGATADO());
            System.out.printf("Saldo apos o resgate: R$ %.2f%n", aplicacao.getVALORATUALLIQUIDO());
        }
    }
}
