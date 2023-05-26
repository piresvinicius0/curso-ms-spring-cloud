package br.com.vplabs.msavaliadorcredito.application;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("avaliacoescredito")
public class AvaliadorCreditoController {

    @GetMapping
    public String status() {
        return "OK";
    }
}
