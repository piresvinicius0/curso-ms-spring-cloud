package br.com.vplabs.msavaliadorcredito.application.ex;

public class DadosClientNotFoundException extends Exception {
    public DadosClientNotFoundException() {
        super("Dados do Cliente não encontrados para o CPF informado!");
    }
}
