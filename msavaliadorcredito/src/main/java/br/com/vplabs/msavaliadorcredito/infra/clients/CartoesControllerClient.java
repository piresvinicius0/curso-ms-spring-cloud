package br.com.vplabs.msavaliadorcredito.infra.clients;

import br.com.vplabs.msavaliadorcredito.domain.model.Cartao;
import br.com.vplabs.msavaliadorcredito.domain.model.CartaoCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "mscartoes", path = "/cartoes")
public interface CartoesControllerClient {
    @GetMapping(params = "cpf")
    ResponseEntity<List<CartaoCliente>> getCartoesByCliente(@RequestParam("cpf") String cpf);
    @GetMapping(params = "renda")
    ResponseEntity<List<Cartao>> getCartoesRendaAte(@RequestParam("renda") Long renda);
}
