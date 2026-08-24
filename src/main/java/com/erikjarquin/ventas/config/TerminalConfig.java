package com.erikjarquin.ventas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
//@Configuration
@ConfigurationProperties(prefix = "payment.terminal")
public class TerminalConfig {
    private String type; //"PHYSUCAL", "SIMULATED"
    private String host; //Ip de la terminal
    private int port; //Puerto
    private String merchantId; // ID del comercio
    private String terminalId; // ID de la ternimal
    private int timeout; // Tiempo de espera(segundos)
    private boolean sslEnabled; // Usar SSL
    private String keystorePath; // Ruta del keystore (certificados)
    private String keystorePassword; // Contraseña del keystore
}
