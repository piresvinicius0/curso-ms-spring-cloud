package br.com.vplabs.msavaliadorcredito.application;

import br.com.vplabs.msavaliadorcredito.application.ex.DadosClientNotFoundException;
import br.com.vplabs.msavaliadorcredito.application.ex.ErroComunicacaoMicroservicesException;
import br.com.vplabs.msavaliadorcredito.domain.model.CartaoCliente;
import br.com.vplabs.msavaliadorcredito.domain.model.DadosCliente;
import br.com.vplabs.msavaliadorcredito.domain.model.SituacaoCliente;
import br.com.vplabs.msavaliadorcredito.infra.clients.CartoesControllerClient;
import br.com.vplabs.msavaliadorcredito.infra.clients.ClienteControllerClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliadorCreditoService {

    private final ClienteControllerClient clientesClient;
    private final CartoesControllerClient cartoesClient;

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
}
