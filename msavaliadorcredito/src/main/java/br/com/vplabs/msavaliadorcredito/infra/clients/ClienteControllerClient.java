package br.com.vplabs.msavaliadorcredito.infra.clients;

import br.com.vplabs.msavaliadorcredito.domain.model.DadosCliente;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.image.DataBufferDouble;

@FeignClient(value = "msclientes", path = "/clientes")
public interface ClienteControllerClient {

    @GetMapping(params = "cpf")
    ResponseEntity<DadosCliente> dadosCliente(@RequestParam("cpf") String cpf);
}
