package com.kaylainevieira.desafio.gestaocontaspagar.application.query;

import com.kaylainevieira.desafio.gestaocontaspagar.domain.model.ContaPagar;

import java.util.UUID;

public interface IContaPagarQueryService {

    ContaPagar buscarContaPagarPorId(UUID id);
}
