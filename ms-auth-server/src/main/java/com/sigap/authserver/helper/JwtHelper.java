package com.sigap.authserver.helper;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sigap.authserver.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtHelper {
    private final JwtProperties jwtProperties;

    public String generateToken(Long userId, String username, List<String> roles){
        try{
            Instant now = Instant.now();
            Instant expiration = now.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(jwtProperties.getIssuer())
                    .subject(username)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiration))
                    .claim("uid", userId)
                    .claim("roles", roles)
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(new MACSigner(secretBytes()));
            return signedJWT.serialize();
        }catch (JOSEException exception){
            throw new IllegalStateException("No fue posible generar el JWT.", exception);
        }

    }

    public long getExpirationMinutes() {
        return jwtProperties.getExpirationMinutes();
    }

    private byte[] secretBytes() {
        byte[] bytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("El secreto JWT debe tener al menos 32 bytes.");
        }
        return bytes;
    }
}
