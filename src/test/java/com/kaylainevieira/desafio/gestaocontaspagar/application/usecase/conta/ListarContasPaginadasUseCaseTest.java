package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarFiltroDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.PageResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql({"classpath:db/sql/application/usecase/conta/listar_contas_paginadas_test.sql"})
class ListarContasPaginadasUseCaseTest {

    @Autowired
    private ListarContasPaginadasUseCase listarContasPaginadasUseCase;

    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @Test
    @DisplayName("Deve listar contas paginadas com filtro de data e descrição com sucesso")
    void listarContasPaginadasUseCase_01() {
        var filtro = ContaPagarFiltroDTO.builder()
                .dataVencimentoInicio(LocalDate.of(2025, 7, 1))
                .dataVencimentoFim(LocalDate.of(2025, 7, 31))
                .descricao("Conta")
                .build();

        var pageable = PageRequest.of(0, 2, Sort.by("dataVencimento").ascending());

        PageResponseDTO<ContaPagarResponseDTO> result = listarContasPaginadasUseCase.execute(filtro, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();

        assertThat(result.getContent())
                .extracting(ContaPagarResponseDTO::getDescricao, ContaPagarResponseDTO::getDataVencimento)
                .containsExactly(
                        tuple("Conta de Gás Julho", LocalDate.of(2025, 7, 5)),
                        tuple("Conta de Luz Julho", LocalDate.of(2025, 7, 10))
                );
    }

    @Test
    @DisplayName("Deve listar todas as contas paginadas sem filtro de descrição")
    void listarContasPaginadasUseCase_02() {
        var filtro = ContaPagarFiltroDTO.builder()
                .dataVencimentoInicio(LocalDate.of(2025, 7, 1))
                .dataVencimentoFim(LocalDate.of(2025, 8, 31))
                .descricao("")
                .build();

        var pageable = PageRequest.of(0, 5, Sort.by("dataVencimento").ascending());

        PageResponseDTO<ContaPagarResponseDTO> result = listarContasPaginadasUseCase.execute(filtro, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(1);

        assertThat(result.getContent())
                .extracting(ContaPagarResponseDTO::getDescricao, ContaPagarResponseDTO::getDataVencimento)
                .containsExactly(
                        tuple("Conta de Gás Julho", LocalDate.of(2025, 7, 5)),
                        tuple("Conta de Luz Julho", LocalDate.of(2025, 7, 10)),
                        tuple("Conta de Água Julho", LocalDate.of(2025, 7, 15)),
                        tuple("Assinatura Internet", LocalDate.of(2025, 7, 20)),
                        tuple("Aluguel Agosto", LocalDate.of(2025, 8, 5))
                );
    }

    @Test
    @DisplayName("Deve retornar página vazia se nenhum filtro corresponder")
    void listarContasPaginadasUseCase_03() {
        var filtro = ContaPagarFiltroDTO.builder()
                .dataVencimentoInicio(LocalDate.of(2026, 1, 1))
                .dataVencimentoFim(LocalDate.of(2026, 1, 31))
                .descricao("NaoExiste")
                .build();

        var pageable = PageRequest.of(0, 10);

        PageResponseDTO<ContaPagarResponseDTO> result = listarContasPaginadasUseCase.execute(filtro, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }
}