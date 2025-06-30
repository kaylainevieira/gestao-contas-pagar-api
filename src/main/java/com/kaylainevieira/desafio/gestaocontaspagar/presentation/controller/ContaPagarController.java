package com.kaylainevieira.desafio.gestaocontaspagar.presentation.controller;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.AlterarSituacaoRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarRequestDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarFiltroDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.PageResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.ICadastrarContaUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IAtualizarContaUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IAlterarSituacaoContaUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IListarContasPaginadasUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IObterContaPorIdUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IObterValorTotalPagoPorPeriodoUseCase;
import com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta.IImportarContasCsvUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contas-a-pagar")
@RequiredArgsConstructor
public class ContaPagarController {

    private final ICadastrarContaUseCase cadastrarContaUseCase;
    private final IAtualizarContaUseCase atualizarContaUseCase;
    private final IAlterarSituacaoContaUseCase alterarSituacaoContaUseCase;
    private final IListarContasPaginadasUseCase listarContasPaginadasUseCase;
    private final IObterContaPorIdUseCase obterContaPorIdUseCase;
    private final IObterValorTotalPagoPorPeriodoUseCase obterValorTotalPagoPorPeriodoUseCase;
    private final IImportarContasCsvUseCase importarContasCsvUseCase;

    @PostMapping
    public ResponseEntity<ContaPagarResponseDTO> cadastrarConta(
            @Valid @RequestBody ContaPagarRequestDTO requestDTO) {
        var novaConta = cadastrarContaUseCase.execute(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaConta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaPagarResponseDTO> atualizarConta(
            @PathVariable UUID id,
            @Valid @RequestBody ContaPagarRequestDTO requestDTO) {
        var contaAtualizada = atualizarContaUseCase.execute(id, requestDTO);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ContaPagarResponseDTO> alterarSituacaoConta(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarSituacaoRequestDTO requestDTO) {
        var contaAtualizada = alterarSituacaoContaUseCase.execute(id, requestDTO);
        return ResponseEntity.ok(contaAtualizada);
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponseDTO<ContaPagarResponseDTO>> buscarContasPaginadasComFiltro(
            @RequestBody ContaPagarFiltroDTO filtro,
            Pageable pageable) {
        var contasPaginadas = listarContasPaginadasUseCase.execute(filtro, pageable);
        return ResponseEntity.ok(contasPaginadas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaPagarResponseDTO> obterContaPorId(@PathVariable UUID id) {
        var conta = obterContaPorIdUseCase.execute(id);
        return ResponseEntity.ok(conta);
    }

    @GetMapping("/valor-total-pago")
    public ResponseEntity<BigDecimal> obterValorTotalPagoPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamentoFim) {
        var totalPago = obterValorTotalPagoPorPeriodoUseCase.execute(dataPagamentoInicio, dataPagamentoFim);
        return ResponseEntity.ok(totalPago);
    }

    @PostMapping("/importar")
    public ResponseEntity<List<ContaPagarResponseDTO>> importarContasCsv(@RequestParam("file") MultipartFile file) {
        var contasImportadas = importarContasCsvUseCase.execute(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(contasImportadas);
    }
}