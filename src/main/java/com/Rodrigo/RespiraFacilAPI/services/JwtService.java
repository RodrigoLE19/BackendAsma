package com.Rodrigo.RespiraFacilAPI.services;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        byte[] clave = Base64.getDecoder().decode(secret);

        if (clave.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET debe contener al menos 32 bytes."
            );
        }

        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "La duración del token debe ser mayor que cero."
            );
        }

        SecretKey secretKey = new SecretKeySpec(clave, "HmacSHA256");

        this.encoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );

        this.expirationMinutes = expirationMinutes;
    }

    public String generarToken(Integer idUsuario) {
        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("respira-facil")
                .subject(idUsuario.toString())
                .issuedAt(ahora)
                .expiresAt(
                        ahora.plus(expirationMinutes, ChronoUnit.MINUTES)
                )
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}