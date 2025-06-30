package com.kaylainevieira.desafio.gestaocontaspagar.application.usecase.conta;

import com.kaylainevieira.desafio.gestaocontaspagar.application.dto.ContaPagarResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImportarContasCsvUseCase {

    List<ContaPagarResponseDTO> execute(MultipartFile file);
}
