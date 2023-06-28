package br.com.vplabs.msavaliadorcredito.application;

import br.com.vplabs.msavaliadorcredito.application.ex.DadosClientNotFoundException;
import br.com.vplabs.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import br.com.vplabs.msavaliadorcredito.application.ex.ErroSolicitacaoCartaoException;
import br.com.vplabs.msavaliadorcredito.domain.model.*;
import br.com.vplabs.msavaliadorcredito.infra.clients.CartoesControllerClient;
import br.com.vplabs.msavaliadorcredito.infra.clients.ClienteControllerClient;
import br.com.vplabs.msavaliadorcredito.infra.clients.infra.mqueue.SolicitacaoEmissaoCartaoPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteControllerClient clientesClient;
    private final CartoesControllerClient cartoesClient;
    private final SolicitacaoEmissaoCartaoPublisher publisher;

    public SituacaoCliente obterSituacaoCliente(String cpf) throws DadosClientNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<CartaoCliente>> cartoesResponse = cartoesClient.getCartoesByCliente(cpf);

            return SituacaoCliente
                    .builder()
                    .cliente(dadosClienteResponse.getBody())
                    .cartoes(cartoesResponse.getBody())
                    .build();
        } catch (FeignException.FeignClientException e) {
            int status = e.status();
            if (HttpStatus.NOT_FOUND.value() == status) {
                throw new DadosClientNotFoundException();
            }
            throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
        }
    }

    public RetornoAvaliacaoCliente realizarAvaliacao(String cpf, Long renda) throws DadosClientNotFoundException, ErroComunicacaoMicroservicesException {
        try {
            ResponseEntity<DadosCliente> dadosClienteResponse = clientesClient.dadosCliente(cpf);
            ResponseEntity<List<Cartao>> cartoesResponse = cartoesClient.getCartoesRendaAte(renda);

            List<Cartao> cartoes = cartoesResponse.getBody();
            var listaCartoesAprovado = cartoes.stream().map(cartao -> {

                DadosCliente dadosCliente = dadosClienteResponse.getBody();
                BigDecimal limiteBasico = cartao.getLimite();
                BigDecimal rendaBD = BigDecimal.valueOf(renda);
                BigDecimal idadeBD = BigDecimal.valueOf(dadosCliente.getIdade());
                var fator = idadeBD.divide(BigDecimal.TEN);
                BigDecimal limiteAprovado = fator.multiply(limiteBasico);

                CartaoAprovado aprovado = new CartaoAprovado();
                aprovado.setCartao(cartao.getNome());
                aprovado.setBandeira(cartao.getBandeira());
                aprovado.setLimiteAprovado(limiteAprovado);

                return aprovado;
            }).collect(Collectors.toList());
            return new RetornoAvaliacaoCliente(listaCartoesAprovado);
        } catch (FeignException.FeignClientException e) {
        int status = e.status();
        if (HttpStatus.NOT_FOUND.value() == status) {
            throw new DadosClientNotFoundException();
        }
        throw new ErroComunicacaoMicroservicesException(e.getMessage(), status);
    }
    }

    public ProtocoloSolicitacaoCartao solicitarEmissaoCartao(DadosSolicitacaoEmissaoCartao dados) {
        try {
            publisher.solicitarCartao(dados);
            var protocolo = UUID.randomUUID().toString();
            return new ProtocoloSolicitacaoCartao(protocolo);
        } catch (Exception e) {
            throw new ErroSolicitacaoCartaoException(e.getMessage());
        }
    }
}
