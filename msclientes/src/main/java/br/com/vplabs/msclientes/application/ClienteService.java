package br.com.vplabs.msclientes.application;

import br.com.vplabs.msclientes.domain.Cliente;
import br.com.vplabs.msclientes.infra.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;

    @Transactional
    public Cliente save(Cliente cliente) {
        return repository.save(cliente);
    }

    public Optional<Cliente> getByCpf(String cpf) {
        return repository.findByCpf(cpf);
    }
}
