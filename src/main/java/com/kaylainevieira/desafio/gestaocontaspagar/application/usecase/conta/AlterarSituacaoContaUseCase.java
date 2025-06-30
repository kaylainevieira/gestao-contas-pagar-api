package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.AlterarSituacaoRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.application.query.ContaPagarQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlterarSituacaoContaUseCase implements IAlterarSituacaoContaUseCase {

    private final ContaPagarMapper contaPagarMapper;
    private final ContaPagarQueryService contaPagarQueryService;

    @Override
    @Transactional
    public ContaPagarResponseDTO execute(UUID id, AlterarSituacaoRequestDTO dto) {
        var contaExistente = contaPagarQueryService.buscarContaPagarPorId(id);

        try {
            contaExistente.alterarSituacaoPara(dto.getNovaSituacao());
        } catch (IllegalStateException e) {
            throw new ContaPagarRegraDeNegocioException(e.getMessage());
        }

        return contaPagarMapper.toContaPagarResponseDTO(contaExistente);
    }
}