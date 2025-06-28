package com.kaylainevieira.desafio.gestaocontaspagar.presentation.controller;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.*;
import com.kaylainevieira.desafio.gestaocontaspagar.application.service.ContaPagarApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/contas-a-pagar")
@RequiredArgsConstructor
public class ContaPagarController {

    private final ContaPagarApplicationService contaPagarApplicationService;

    @PostMapping
    public ResponseEntity<ContaPagarResponseDTO> cadastrarConta(
            @Valid @RequestBody ContaPagarRequestDTO requestDTO) {
        var novaConta = contaPagarApplicationService.cadastrarConta(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaPagarResponseDTO> atualizarConta(
            @PathVariable UUID id,
            @Valid @RequestBody ContaPagarRequestDTO requestDTO) {
        var contaAtualizada = contaPagarApplicationService.atualizarConta(id, requestDTO);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContaPagarResponseDTO> alterarSituacaoConta(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarSituacaoRequestDTO requestDTO) {
        var contaAtualizada = contaPagarApplicationService.alterarSituacaoConta(id, requestDTO);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponseDTO<ContaPagarResponseDTO>> buscarContasPaginadasComFiltro(
            @RequestBody ContaPagarFiltroDTO filtro,
            Pageable pageable) {
        var contasPaginadas = contaPagarApplicationService.obterContasPaginadasComFiltro(filtro, pageable);
        return ResponseEntity.ok(contasPaginadas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaPagarResponseDTO> obterContaPorId(@PathVariable UUID id) {
        var conta = contaPagarApplicationService.obterContaPorId(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/valor-total-pago")
    public ResponseEntity<BigDecimal> obterValorTotalPagoPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoFim) {
        var totalPago = contaPagarApplicationService.obterValorTotalPagoPorPeriodo(dataPagamentoInicio, dataPagamentoFim);
        return ResponseEntity.ok(totalPago);
    }
}