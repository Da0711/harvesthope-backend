package com.harvesthope.model.dto;

import com.harvesthope.model.entity.StatusChamado;

public record AtualizarStatusChamadoRequest(
        StatusChamado status
) {}