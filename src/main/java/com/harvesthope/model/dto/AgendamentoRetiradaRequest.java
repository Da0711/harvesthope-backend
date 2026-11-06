
package com.harvesthope.model.dto;

import java.time.LocalDateTime;

public class AgendamentoRetiradaRequest {

    private LocalDateTime dataRetirada;
    private String localRetirada;

    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public String getLocalRetirada() {
        return localRetirada;
    }

    public void setLocalRetirada(String localRetirada) {
        this.localRetirada = localRetirada;
    }
}
