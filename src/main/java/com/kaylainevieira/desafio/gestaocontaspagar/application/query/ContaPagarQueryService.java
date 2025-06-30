package com.kaylainevieira.desafio.gestaocontaspagar.application.query;

import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContaPagarQueryService implements IContaPagarQueryService {

    private final ContaPagarRepository contaPagarRepository;

    @Override
    @Transactional(readOnly = true)
    public ContaPagar buscarContaPagarPorId(UUID id) {
        return contaPagarRepository.findById(id)
                .orElseThrow(() -> new ContaPagarNaoEncontradaException("Conta a pagar com ID " + id + " não encontrada."));
    }
}
