package com.kaylainevieira.desafio.gestaocontaspagar.application.service;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.*;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarRegraDeNegocioException;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.DadosAtualizacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaPagarApplicationService {

    private final ContaPagarRepository contaPagarRepository;

    @Transactional
    public ContaPagarResponseDTO cadastrarConta(ContaPagarRequestDTO dto) {
        var novaConta = ContaPagar.builder()
                .dataVencimento(dto.getDataVencimento())
                .valor(dto.getValor())
                .descricao(dto.getDescricao())
                .situacao(SituacaoConta.PENDENTE)
                .build();

        var contaSalva = contaPagarRepository.save(novaConta);

        return toContaPagarResponseDTO(contaSalva);
    }

    @Transactional
    public ContaPagarResponseDTO atualizarConta(UUID id, ContaPagarRequestDTO dto) {
        var contaExistente = buscarContaPagarPorId(id);

        var dadosAtualizacao = DadosAtualizacaoConta.builder()
                .dataVencimento(dto.getDataVencimento())
                .valor(dto.getValor())
                .descricao(dto.getDescricao())
                .build();

        try {
            contaExistente.atualizarDadosCadastrais(dadosAtualizacao);
        } catch (ContaPagarRegraDeNegocioException e) {
            throw new ContaPagarRegraDeNegocioException(e.getMessage());
        }

        return toContaPagarResponseDTO(contaExistente);
    }

    @Transactional
    public ContaPagarResponseDTO alterarSituacaoConta(UUID id, AlterarSituacaoRequestDTO dto) {
        var contaExistente = buscarContaPagarPorId(id);

        try {
            contaExistente.alterarSituacaoPara(dto.getNovaSituacao());
        } catch (ContaPagarRegraDeNegocioException e) {
            throw new ContaPagarRegraDeNegocioException(e.getMessage());
        }

        return toContaPagarResponseDTO(contaExistente);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ContaPagarResponseDTO> obterContasPaginadasComFiltro(
            ContaPagarFiltroDTO filtro,
            Pageable pageable) {

        var contasPage = contaPagarRepository.findByDataVencimentoBetweenAndDescricaoContainingIgnoreCase(
                filtro.getDataVencimentoInicio(),
                filtro.getDataVencimentoFim(),
                filtro.getDescricao(),
                pageable);

        var contentDTO = contasPage.getContent().stream()
                .map(this::toContaPagarResponseDTO)
                .collect(Collectors.toList());

        return PageResponseDTO.<ContaPagarResponseDTO>builder()
                .content(contentDTO)
                .pageNumber(contasPage.getNumber())
                .pageSize(contasPage.getSize())
                .totalElements(contasPage.getTotalElements())
                .totalPages(contasPage.getTotalPages())
                .isLast(contasPage.isLast())
                .isFirst(contasPage.isFirst())
                .build();
    }

    @Transactional(readOnly = true)
    public ContaPagarResponseDTO obterContaPorId(UUID id) {
        var conta = buscarContaPagarPorId(id);
        return toContaPagarResponseDTO(conta);
    }

    @Transactional(readOnly = true)
    public BigDecimal obterValorTotalPagoPorPeriodo(LocalDate dataPagamentoInicio, LocalDate dataPagamentoFim) {
        if (dataPagamentoInicio == null || dataPagamentoFim == null) {
            throw new ContaPagarRegraDeNegocioException("As datas de início e fim do pagamento são obrigatórias para obter o valor total pago por período.");
        }
        if (dataPagamentoInicio.isAfter(dataPagamentoFim)) {
            throw new ContaPagarRegraDeNegocioException("A data de início do pagamento não pode ser posterior à data de fim do pagamento.");
        }

        return contaPagarRepository.sumValorByDataPagamentoBetweenAndSituacao(
                dataPagamentoInicio,
                dataPagamentoFim,
                SituacaoConta.PAGA);
    }

    private ContaPagar buscarContaPagarPorId(UUID id) {
        return contaPagarRepository.findById(id)
                .orElseThrow(() -> new ContaPagarNaoEncontradaException("Conta a pagar com ID " + id + " não encontrada."));
    }

    private ContaPagarResponseDTO toContaPagarResponseDTO(ContaPagar conta) {
        return ContaPagarResponseDTO.builder()
                .id(conta.getId())
                .dataVencimento(conta.getDataVencimento())
                .dataPagamento(conta.getDataPagamento())
                .valor(conta.getValor())
                .descricao(conta.getDescricao())
                .situacao(conta.getSituacao())
                .dataCadastro(conta.getDataCadastro())
                .build();
    }
}