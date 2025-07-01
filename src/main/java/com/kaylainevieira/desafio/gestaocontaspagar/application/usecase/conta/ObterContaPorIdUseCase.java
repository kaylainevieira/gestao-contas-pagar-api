package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.application.query.ContaPagarQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObterContaPorIdUseCase implements IObterContaPorIdUseCase {

    private final ContaPagarQueryService contaPagarQueryService;
    private final ContaPagarMapper contaPagarMapper;

    @Override
    @Transactional(readOnly = true)
    public ContaPagarResponseDTO execute(UUID id) {
        var conta = contaPagarQueryService.buscarContaPagarPorId(id);
        return contaPagarMapper.toContaPagarResponseDTO(conta);
    }
}
