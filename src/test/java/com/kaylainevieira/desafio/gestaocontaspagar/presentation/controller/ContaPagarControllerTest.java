package com.kaylainevieira.desafio.gestaocontaspagar.presentation.controller;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.*;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarNaoEncontradaException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.*;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.SituacaoConta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaPagarControllerTest {

    @Mock
    private ICadastrarContaUseCase cadastrarContaUseCase;

    @Mock
    private IAtualizarContaUseCase atualizarContaUseCase;

    @Mock
    private IAlterarSituacaoContaUseCase alterarSituacaoContaUseCase;

    @Mock
    private IListarContasPaginadasUseCase listarContasPaginadasUseCase;

    @Mock
    private IObterContaPorIdUseCase obterContaPorIdUseCase;

    @Mock
    private IObterValorTotalPagoPorPeriodoUseCase obterValorTotalPagoPorPeriodoUseCase;

    @Mock
    private IImportarContasCsvUseCase importarContasCsvUseCase;

    @InjectMocks
    private ContaPagarController contaPagarController;

    @Test
    @DisplayName("POST /api/contas-a-pagar - Deve cadastrar uma nova conta com sucesso")
    void cadastrarConta_01() {
        var inputRequestDTO = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Conta de Teste")
                .build();

        var testAccountId = UUID.randomUUID();
        var expectedResponseDTO = ContaPagarResponseDTO.builder()
                .id(testAccountId)
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Conta de Teste")
                .situacao(SituacaoConta.PENDENTE)
                .dataCadastro(OffsetDateTime.now())
                .build();

        when(cadastrarContaUseCase.execute(any(ContaPagarRequestDTO.class))).thenReturn(expectedResponseDTO);

        var responseEntity = contaPagarController.cadastrarConta(inputRequestDTO);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponseDTO);
        verify(cadastrarContaUseCase, times(1)).execute(inputRequestDTO);
    }

    @Test
    @DisplayName("PUT /api/contas-a-pagar/{id} - Deve atualizar uma conta com sucesso")
    void atualizarConta_01() {
        var testId = UUID.randomUUID();
        var inputRequestDTO = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Descricao Atualizada")
                .build();
        var expectedResponseDTO = ContaPagarResponseDTO.builder()
                .id(testId)
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Descricao Atualizada")
                .situacao(SituacaoConta.PENDENTE)
                .dataCadastro(OffsetDateTime.now())
                .build();

        when(atualizarContaUseCase.execute(eq(testId), any(ContaPagarRequestDTO.class))).thenReturn(expectedResponseDTO);

        var responseEntity = contaPagarController.atualizarConta(testId, inputRequestDTO);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponseDTO);
        verify(atualizarContaUseCase, times(1)).execute(testId, inputRequestDTO);
    }

    @Test
    @DisplayName("PUT /api/contas-a-pagar/{id} - Deve retornar 404 Not Found se a conta não existir")
    void atualizarConta_02_notFound() {
        var nonExistentId = UUID.randomUUID();
        var inputRequestDTO = ContaPagarRequestDTO.builder()
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Descricao Inexistente")
                .build();

        when(atualizarContaUseCase.execute(eq(nonExistentId), any(ContaPagarRequestDTO.class)))
                .thenThrow(new ContaPagarNaoEncontradaException("Conta não encontrada."));

        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                contaPagarController.atualizarConta(nonExistentId, inputRequestDTO));

        verify(atualizarContaUseCase, times(1)).execute(nonExistentId, inputRequestDTO);
    }

    @Test
    @DisplayName("PATCH /api/contas-a-pagar/{id}/situacao - Deve alterar a situação com sucesso")
    void alterarSituacaoConta_01() {
        var testId = UUID.randomUUID();
        var alterarSituacaoDTO = AlterarSituacaoRequestDTO.builder().novaSituacao(SituacaoConta.PAGA).build();
        var expectedResponseDTO = ContaPagarResponseDTO.builder()
                .id(testId)
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Conta de Teste")
                .situacao(SituacaoConta.PAGA)
                .dataCadastro(OffsetDateTime.now())
                .build();

        when(alterarSituacaoContaUseCase.execute(eq(testId), any(AlterarSituacaoRequestDTO.class))).thenReturn(expectedResponseDTO);

        var responseEntity = contaPagarController.alterarSituacaoConta(testId, alterarSituacaoDTO);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponseDTO);
        verify(alterarSituacaoContaUseCase, times(1)).execute(testId, alterarSituacaoDTO);
    }

    @Test
    @DisplayName("GET /api/contas-a-pagar/{id} - Deve obter conta por ID com sucesso")
    void obterContaPorId_01() {
        var testId = UUID.randomUUID();
        var expectedResponseDTO = ContaPagarResponseDTO.builder()
                .id(testId)
                .dataVencimento(LocalDate.of(2025, 1, 1))
                .valor(new BigDecimal("100.00"))
                .descricao("Conta de Teste")
                .situacao(SituacaoConta.PENDENTE)
                .dataCadastro(OffsetDateTime.now())
                .build();

        when(obterContaPorIdUseCase.execute(eq(testId))).thenReturn(expectedResponseDTO);

        var responseEntity = contaPagarController.obterContaPorId(testId);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(expectedResponseDTO);
        verify(obterContaPorIdUseCase, times(1)).execute(testId);
    }

    @Test
    @DisplayName("GET /api/contas-a-pagar/{id} - Deve retornar 404 Not Found se a conta não existir")
    void obterContaPorId_02_notFound() {
        var nonExistentId = UUID.randomUUID();
        when(obterContaPorIdUseCase.execute(eq(nonExistentId)))
                .thenThrow(new ContaPagarNaoEncontradaException("Conta não encontrada."));

        assertThrows(ContaPagarNaoEncontradaException.class, () ->
                contaPagarController.obterContaPorId(nonExistentId));

        verify(obterContaPorIdUseCase, times(1)).execute(nonExistentId);
    }

    @Test
    @DisplayName("POST /api/contas-a-pagar/search - Deve listar contas paginadas com filtro")
    void buscarContasPaginadasComFiltro_01() {
        var filtroDTO = ContaPagarFiltroDTO.builder()
                .dataVencimentoInicio(LocalDate.of(2025, 1, 1))
                .dataVencimentoFim(LocalDate.of(2025, 12, 31))
                .descricao("Teste")
                .build();

        var pageable = PageRequest.of(0, 10);
        var pageResponse = PageResponseDTO.<ContaPagarResponseDTO>builder()
                .content(Collections.singletonList(ContaPagarResponseDTO.builder().id(UUID.randomUUID()).build()))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .build();

        when(listarContasPaginadasUseCase.execute(any(ContaPagarFiltroDTO.class), any(Pageable.class))).thenReturn(pageResponse);

        var responseEntity = contaPagarController.buscarContasPaginadasComFiltro(filtroDTO, pageable);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(pageResponse);
        verify(listarContasPaginadasUseCase, times(1)).execute(filtroDTO, pageable);
    }

    @Test
    @DisplayName("GET /api/contas-a-pagar/valor-total-pago - Deve obter valor total pago por período")
    void obterValorTotalPagoPorPeriodo_01() {
        var dataInicio = LocalDate.of(2025, 1, 1);
        var dataFim = LocalDate.of(2025, 12, 31);
        var totalPagoEsperado = new BigDecimal("1500.75");

        when(obterValorTotalPagoPorPeriodoUseCase.execute(any(LocalDate.class), any(LocalDate.class))).thenReturn(totalPagoEsperado);

        var responseEntity = contaPagarController.obterValorTotalPagoPorPeriodo(dataInicio, dataFim);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(totalPagoEsperado);
        verify(obterValorTotalPagoPorPeriodoUseCase, times(1)).execute(dataInicio, dataFim);
    }

    @Test
    @DisplayName("POST /api/contas-a-pagar/importar - Deve importar CSV com sucesso")
    void importarContasCsv_01() throws Exception {
        var csvContent = "descricao,valor,data_vencimento,data_pagamento,situacao\n" +
                "Teste Import,100.00,2025-01-01,,PENDENTE";

        var mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        var importedResponseList = Collections.singletonList(ContaPagarResponseDTO.builder().id(UUID.randomUUID()).build());
        when(importarContasCsvUseCase.execute(any(MultipartFile.class))).thenReturn(importedResponseList);

        var responseEntity = contaPagarController.importarContasCsv(mockFile);

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(importedResponseList);
        verify(importarContasCsvUseCase, times(1)).execute(mockFile);
    }
}
