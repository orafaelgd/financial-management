import java.util.HashSet;
import java.util.Set;

public class AplicacaoFinanceira {
    private static final Set<String> numerosCadastrados = new HashSet<>();

    protected String NOMEAPLICACAO, NUMEROAPLICACAPFINANCEIRA;
    protected double VALORAPLICADO, VALORRESGATADO, VALORATUALBRUTO, VALORATUALLIQUIDO;
    protected double RENDIMENTOATUALBRUTO, RENDIMENTOATUALLIQUIDO;
    protected double TAXADIARIA, TAXAMENSAL;
    protected int TIPORENDIMENTO;
    protected int TEMPOFINALINVESTIMENTO, TEMPOMINIMOPERMANENCIA;
    protected int TIPORESGATE, IOF, TIPOIMPOSTO;
    protected int TEMPOATUALIZADOAPLICACAO, TEMPOATUALIZADOEFETIVO;
    protected double IMPOSTOATUALDEVIDO;
    protected int DIA, MES, ANO;

    // 0 - nao cadastrada; 1 - ativa; 2 - bloqueada; 3 - cancelada.
    protected int STATUS;
    // Dias efetivos que ainda nao foram usados no calculo dos rendimentos.
    protected int diasCalculos;

    public AplicacaoFinanceira() {
        NOMEAPLICACAO = "";
        NUMEROAPLICACAPFINANCEIRA = "";
        // Os atributos numericos comecam em zero por padrao no Java.
    }

    public int setCadastro(String nmap, String nap, double vlap, double txdia, double txmes,
                           int tiporend, int tmpfinal, int tempomin, int tiporesg,
                           int tipoiof, int tipoimposto, int dd, int mm, int aaaa) {
        if (nmap == null || nmap.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da aplicacao.");
        }
        validarValor(vlap);
        validarValor(txdia);
        validarValor(txmes);
        validarTipo(tiporend, 5);
        validarTipo(tiporesg, 4);
        validarTipo(tipoiof, 2);
        validarTipo(tipoimposto, 1);
        if (tmpfinal <= 0 || tempomin < 0 || tempomin > tmpfinal) {
            throw new IllegalArgumentException("Prazos da aplicacao invalidos.");
        }

        setNUMEROAPLICACAPFINANCEIRA(nap);
        NOMEAPLICACAO = nmap;
        VALORAPLICADO = vlap;
        VALORRESGATADO = 0;
        VALORATUALBRUTO = vlap;
        VALORATUALLIQUIDO = vlap;
        RENDIMENTOATUALBRUTO = 0;
        RENDIMENTOATUALLIQUIDO = 0;
        // Se as duas taxas forem informadas, a mensal tem prioridade.
        if (txmes > 0) {
            setTAXAMENSAL(txmes);
        } else {
            setTAXADIARIA(txdia);
        }
        TIPORENDIMENTO = tiporend;
        TEMPOFINALINVESTIMENTO = tmpfinal;
        TEMPOMINIMOPERMANENCIA = tempomin;
        TIPORESGATE = tiporesg;
        IOF = tipoiof;
        TIPOIMPOSTO = tipoimposto;
        TEMPOATUALIZADOAPLICACAO = 0;
        TEMPOATUALIZADOEFETIVO = 0;
        IMPOSTOATUALDEVIDO = 0;
        DIA = dd;
        MES = mm;
        ANO = aaaa;
        STATUS = 1;
        diasCalculos = 0;
        return STATUS;
    }

    // Mantem os nomes dos metodos que ja existiam no projeto.
    public int setCadastroAPLICACAOFINANCEIRA(String nmap, String nap, double vlap,
                                             double txdia, double txmes, int tiporend,
                                             int tmpfinal, int tempomin, int tiporesg,
                                             int tipoiof, int tipoimposto, int dd, int mm, int aaaa) {
        return setCadastro(nmap, nap, vlap, txdia, txmes, tiporend, tmpfinal,
                tempomin, tiporesg, tipoiof, tipoimposto, dd, mm, aaaa);
    }

    public int setCadastroPOUPANCA(String nap, double vlap, double txmes, int dd, int mm, int aaaa) {
        return setCadastro("POUPANCA", nap, vlap, 0, txmes,
                1, 1000 * 365, 0, 0, 0, 0, dd, mm, aaaa);
    }

    public int setCadastroSELICDIARIA(String nap, double vlap, double txdia, double txmes,
                                     int tmpfinal, int tempomin, int dd, int mm, int aaaa) {
        return setCadastro("SELICDIARIA", nap, vlap, txdia, txmes,
                0, tmpfinal, tempomin, 0, 1, 1, dd, mm, aaaa);
    }

    private int getPeriodo(int tipo) {
        switch (tipo) {
            case 0: return 1;
            case 1: return 30;
            case 2: return 90;
            case 3: return 180;
            case 4: return 365;
            default: return TEMPOFINALINVESTIMENTO;
        }
    }

    public void atualizarSaldo() {
        if (STATUS != 1) {
            return;
        }

        int periodo = getPeriodo(TIPORENDIMENTO);
        double taxaPeriodo = TAXADIARIA;
        switch (TIPORENDIMENTO) {
            case 1: taxaPeriodo = TAXAMENSAL; break;
            case 2: taxaPeriodo = TAXAMENSAL * 3; break;
            case 3: taxaPeriodo = TAXAMENSAL * 6; break;
            case 4: taxaPeriodo = TAXAMENSAL * 12; break;
            case 5:
                // Rendimento diario acumulado, creditado somente no vencimento.
                taxaPeriodo = (Math.pow(1 + TAXADIARIA / 100, periodo) - 1) * 100;
                break;
        }

        while (periodo > 0 && diasCalculos >= periodo) {
            double rendimento = VALORATUALBRUTO * taxaPeriodo / 100;
            RENDIMENTOATUALBRUTO += rendimento;
            VALORATUALBRUTO += rendimento;
            diasCalculos -= periodo;
        }

        // Recalcula o imposto sobre todo o rendimento que permanece aplicado.
        double valorIOF = 0;
        if (IOF == 2 || (IOF == 1 && TEMPOATUALIZADOAPLICACAO < 30)) {
            valorIOF = RENDIMENTOATUALBRUTO * 3.5 / 100;
        }

        double aliquotaIR = 0;
        if (TIPOIMPOSTO == 1) {
            if (TEMPOATUALIZADOAPLICACAO <= 180) {
                aliquotaIR = 22.5;
            } else if (TEMPOATUALIZADOAPLICACAO <= 360) {
                aliquotaIR = 20;
            } else if (TEMPOATUALIZADOAPLICACAO <= 720) {
                aliquotaIR = 17.5;
            } else {
                aliquotaIR = 15;
            }
        }

        double valorIR = (RENDIMENTOATUALBRUTO - valorIOF) * aliquotaIR / 100;
        IMPOSTOATUALDEVIDO = valorIOF + valorIR;
        RENDIMENTOATUALLIQUIDO = RENDIMENTOATUALBRUTO - IMPOSTOATUALDEVIDO;
        VALORATUALLIQUIDO = VALORATUALBRUTO - IMPOSTOATUALDEVIDO;
    }

    public void atualisarSALDO() {
        atualizarSaldo();
    }

    public void incrementarDia() {
        if (STATUS == 1) {
            TEMPOATUALIZADOAPLICACAO++;
        }
    }

    public void incrementarDiaAplicacao() {
        if (STATUS == 1 && TEMPOATUALIZADOEFETIVO < TEMPOFINALINVESTIMENTO) {
            TEMPOATUALIZADOEFETIVO++;
            diasCalculos++;
        }
    }

    public void incrementarDiaAplicação() {
        incrementarDiaAplicacao();
    }

    public void atualizarDiaAplicação(int d, int m, int a) {
        incrementarDiaAplicacao();
    }

    // Recebe o valor liquido desejado. Retorna 1 para sucesso ou 0 para erro.
    public int resgateValor(double valorResgate) {
        if (STATUS != 1 || !Double.isFinite(valorResgate) || valorResgate <= 0
                || TEMPOATUALIZADOAPLICACAO < TEMPOMINIMOPERMANENCIA) {
            return 0;
        }
        int periodo = getPeriodo(TIPORESGATE);
        if (TIPORESGATE != 0 && TEMPOATUALIZADOAPLICACAO < TEMPOFINALINVESTIMENTO
                && (TEMPOATUALIZADOAPLICACAO < periodo
                    || TEMPOATUALIZADOAPLICACAO % periodo != 0)) {
            return 0;
        }

        atualizarSaldo();
        if (valorResgate > VALORATUALLIQUIDO) {
            return 0;
        }

        double parteRestante = 1 - valorResgate / VALORATUALLIQUIDO;
        VALORATUALBRUTO *= parteRestante;
        RENDIMENTOATUALBRUTO *= parteRestante;
        VALORRESGATADO += valorResgate;
        atualizarSaldo();
        return 1;
    }

    private void validarValor(double valor) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException("O valor deve ser um numero maior ou igual a zero.");
        }
    }

    private void validarTipo(int tipo, int maximo) {
        if (tipo < 0 || tipo > maximo) {
            throw new IllegalArgumentException("Tipo invalido.");
        }
    }

    public String getNOMEAPLICACAO() {
        return NOMEAPLICACAO;
    }

    public void setNOMEAPLICACAO(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome da aplicacao.");
        }
        NOMEAPLICACAO = nome;
    }

    public String getNUMEROAPLICACAPFINANCEIRA() {
        return NUMEROAPLICACAPFINANCEIRA;
    }

    public void setNUMEROAPLICACAPFINANCEIRA(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o numero da aplicacao.");
        }
        numero = numero.trim();
        if (numero.equals(NUMEROAPLICACAPFINANCEIRA)) {
            return;
        }
        if (numerosCadastrados.contains(numero)) {
            throw new IllegalArgumentException("Numero de aplicacao ja cadastrado.");
        }
        numerosCadastrados.remove(NUMEROAPLICACAPFINANCEIRA);
        numerosCadastrados.add(numero);
        NUMEROAPLICACAPFINANCEIRA = numero;
    }

    public double getVALORAPLICADO() {
        return VALORAPLICADO;
    }

    public void setVALORAPLICADO(double valor) {
        validarValor(valor);
        VALORAPLICADO = valor;
    }

    public double getVALORRESGATADO() {
        return VALORRESGATADO;
    }

    public void setVALORRESGATADO(double valor) {
        validarValor(valor);
        VALORRESGATADO = valor;
    }

    public double getVALORATUALBRUTO() {
        return VALORATUALBRUTO;
    }

    public void setVALORATUALBRUTO(double valor) {
        validarValor(valor);
        VALORATUALBRUTO = valor;
    }

    public double getVALORATUALLIQUIDO() {
        return VALORATUALLIQUIDO;
    }

    public void setVALORATUALLIQUIDO(double valor) {
        validarValor(valor);
        VALORATUALLIQUIDO = valor;
    }

    public double getRENDIMENTOATUALBRUTO() {
        return RENDIMENTOATUALBRUTO;
    }

    public void setRENDIMENTOATUALBRUTO(double valor) {
        validarValor(valor);
        RENDIMENTOATUALBRUTO = valor;
    }

    public double getRENDIMENTOATUALLIQUIDO() {
        return RENDIMENTOATUALLIQUIDO;
    }

    public void setRENDIMENTOATUALLIQUIDO(double valor) {
        validarValor(valor);
        RENDIMENTOATUALLIQUIDO = valor;
    }

    public double getIMPOSTOATUALDEVIDO() {
        return IMPOSTOATUALDEVIDO;
    }

    public void setIMPOSTOATUALDEVIDO(double valor) {
        validarValor(valor);
        IMPOSTOATUALDEVIDO = valor;
    }

    public double getTAXADIARIA() {
        return TAXADIARIA;
    }

    public void setTAXADIARIA(double taxa) {
        validarValor(taxa);
        TAXADIARIA = taxa;
        TAXAMENSAL = taxa * 30;
    }

    public double getTAXAMENSAL() {
        return TAXAMENSAL;
    }

    public void setTAXAMENSAL(double taxa) {
        validarValor(taxa);
        TAXAMENSAL = taxa;
        TAXADIARIA = taxa / 30;
    }

    public int getTIPORENDIMENTO() {
        return TIPORENDIMENTO;
    }

    public void setTIPORENDIMENTO(int tipo) {
        validarTipo(tipo, 5);
        TIPORENDIMENTO = tipo;
    }

    public int getTIPORESGATE() {
        return TIPORESGATE;
    }

    public void setTIPORESGATE(int tipo) {
        validarTipo(tipo, 4);
        TIPORESGATE = tipo;
    }

    public int getIOF() {
        return IOF;
    }

    public void setIOF(int tipo) {
        validarTipo(tipo, 2);
        IOF = tipo;
    }

    public int getTIPOIMPOSTO() {
        return TIPOIMPOSTO;
    }

    public void setTIPOIMPOSTO(int tipo) {
        validarTipo(tipo, 1);
        TIPOIMPOSTO = tipo;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int tipo) {
        validarTipo(tipo, 3);
        STATUS = tipo;
    }

    public int getTEMPOFINALINVESTIMENTO() {
        return TEMPOFINALINVESTIMENTO;
    }

    public void setTEMPOFINALINVESTIMENTO(int dias) {
        if (dias <= 0 || dias < TEMPOMINIMOPERMANENCIA || dias < TEMPOATUALIZADOEFETIVO) {
            throw new IllegalArgumentException("Prazo final invalido.");
        }
        TEMPOFINALINVESTIMENTO = dias;
    }

    public int getTEMPOMINIMOPERMANENCIA() {
        return TEMPOMINIMOPERMANENCIA;
    }

    public void setTEMPOMINIMOPERMANENCIA(int dias) {
        if (dias < 0 || dias > TEMPOFINALINVESTIMENTO) {
            throw new IllegalArgumentException("Prazo minimo invalido.");
        }
        TEMPOMINIMOPERMANENCIA = dias;
    }

    public int getTEMPOATUALIZADOAPLICACAO() {
        return TEMPOATUALIZADOAPLICACAO;
    }

    public void setTEMPOATUALIZADOAPLICACAO(int dias) {
        validarValor(dias);
        TEMPOATUALIZADOAPLICACAO = dias;
    }

    public int getTEMPOATUALIZADOEFETIVO() {
        return TEMPOATUALIZADOEFETIVO;
    }

    public void setTEMPOATUALIZADOEFETIVO(int dias) {
        int diasJaCalculados = TEMPOATUALIZADOEFETIVO - diasCalculos;
        if (dias < diasJaCalculados || dias > TEMPOFINALINVESTIMENTO) {
            throw new IllegalArgumentException("Dias efetivos invalidos.");
        }
        diasCalculos = dias - diasJaCalculados;
        TEMPOATUALIZADOEFETIVO = dias;
    }

    public int getDIA() {
        return DIA;
    }

    public void setDIA(int valor) {
        DIA = valor;
    }

    public int getMES() {
        return MES;
    }

    public void setMES(int valor) {
        MES = valor;
    }

    public int getANO() {
        return ANO;
    }

    public void setANO(int valor) {
        ANO = valor;
    }

    public int getDiasCalculos() {
        return diasCalculos;
    }

    public void setDiasCalculos(int dias) {
        if (dias < 0 || dias > TEMPOATUALIZADOEFETIVO) {
            throw new IllegalArgumentException("Dias pendentes de calculo invalidos.");
        }
        diasCalculos = dias;
    }
}
