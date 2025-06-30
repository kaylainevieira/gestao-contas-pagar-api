package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarCsvDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import com.kaylainevieira.desafio.gestaocontaspagar.application.exception.ContaPagarCsvException;
import com.kaylainevieira.desafio.gestaocontaspagar.application.mapper.ContaPagarMapper;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;
import com.kaylainevieira.desafio.gestaocontaspagar.domain.repository.ContaPagarRepository;
import com.kaylainevieira.desafio.gestaocontaspagar.infrastructure.csv.ContaPagarCsvParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportarContasCsvUseCase implements IImportarContasCsvUseCase {

    private final ContaPagarRepository contaPagarRepository;
    private final ContaPagarCsvParser contaPagarCsvParser;
    private final ContaPagarMapper contaPagarMapper;

    @Override
    public List<ContaPagarResponseDTO> execute(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ContaPagarCsvException("O arquivo CSV para importação está vazio.");
        }
        if (file.getContentType() == null || !"text/csv".equals(file.getContentType())) {
            throw new ContaPagarCsvException("Formato de arquivo inválido. Apenas arquivos CSV (text/csv) são aceitos.");
        }

        List<ContaPagarCsvDTO> contasCsv;
        try {
            contasCsv = contaPagarCsvParser.parse(file.getInputStream());
        } catch (IOException e) {
            throw new ContaPagarCsvException("Erro ao ler o arquivo CSV: " + e.getMessage());
        }

        if (contasCsv.isEmpty()) {
            throw new ContaPagarCsvException("O arquivo CSV não contém dados válidos para importação após o parsing.");
        }

        var novasContas = contasCsv.stream()
                .map(csvDto -> ContaPagar.builder()
                        .dataVencimento(csvDto.getDataVencimento())
                        .valor(csvDto.getValor())
                        .descricao(csvDto.getDescricao())
                        .situacao(csvDto.getSituacao())
                        .dataPagamento(csvDto.getDataPagamento())
                        .build())
                .collect(Collectors.toList());

        var contasImportadas = contaPagarRepository.saveAll(novasContas);

        return contasImportadas.stream()
                .map(contaPagarMapper::toContaPagarResponseDTO)
                .collect(Collectors.toList());
    }
}
