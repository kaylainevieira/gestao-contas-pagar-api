package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.application.query.ContaPagarQueryService;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.DadosAtualizacaoConta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AtualizarContaUseCase implements IAtualizarContaUseCase {

    private final ContaPagarQueryService contaPagarQueryService;
    private final ContaPagarMapper contaPagarMapper;

    @Override
    @Transactional
    public ContaPagarResponseDTO execute(UUID id, ContaPagarRequestDTO dto) {
        var contaExistente = contaPagarQueryService.buscarContaPagarPorId(id);

        var dadosAtualizacao = DadosAtualizacaoConta.builder()
                .dataVencimento(dto.getDataVencimento())
                .valor(dto.getValor())
                .descricao(dto.getDescricao())
                .build();

        try {
            contaExistente.atualizarDadosCadastrais(dadosAtualizacao);
        } catch (IllegalStateException e) {
            throw new ContaPagarRegraDeNegocioException(e.getMessage());
        }

        return contaPagarMapper.toContaPagarResponseDTO(contaExistente);
    }
}
