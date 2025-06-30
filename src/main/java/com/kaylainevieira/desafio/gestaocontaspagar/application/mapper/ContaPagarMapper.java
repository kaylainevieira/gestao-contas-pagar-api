package com.kaylainevieira.desafio.gestaocontaspagar.application.mapper;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import org.springframework.stereotype.Component;

@Component
public class ContaPagarMapper {

    public ContaPagarResponseDTO toContaPagarResponseDTO(ContaPagar conta) {
        return ContaPagarResponseDTO.builder()
                .id(conta.getId())
                .dataVencimento(conta.getDataVencimento())
                .dataPagamento(conta.getDataPagamento())
                .valor(conta.getValor())
                .descricao(conta.getDescricao())
                .situacao(conta.getSituacao())
                .dataCadastro(conta.getDataCadastro())
                .dataAtualizacao(conta.getDataAtualizacao())
                .build();
    }
}
