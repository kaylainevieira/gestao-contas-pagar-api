package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CadastrarContaUseCase implements ICadastrarContaUseCase {

    private final ContaPagarRepository contaPagarRepository;
    private final ContaPagarMapper contaPagarMapper;

    @Override
    @Transactional
    public ContaPagarResponseDTO execute(ContaPagarRequestDTO dto) {
        var novaConta = ContaPagar.builder()
                .dataVencimento(dto.getDataVencimento())
                .valor(dto.getValor())
                .descricao(dto.getDescricao())
                .situacao(SituacaoConta.PENDENTE)
                .build();
        var contaSalva = contaPagarRepository.save(novaConta);
        return contaPagarMapper.toContaPagarResponseDTO(contaSalva);
    }
}
