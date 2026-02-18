package org.musicservice.demo.support.factory.unit.auth;

import java.time.Duration;

public class JwtTokenFactory {

    private final static String JWT_VALUE = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
            "eyJpc3MiOiJhcHAtbmFtZSIsInN1YiI6IjE2Iiwicm9sZXMiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3NzA3MjczNzksImV4cCI6MTc3MDcyNzQzOSwianRpIjoiMjhiZDI0NmYtN2E0MS00YjFlLWE2NzgtYjBjOTMwNjYwZDRlIn0." +
            "06PaNOBFcQi4pH0uGA81edQj-VCRBdzrr8_X88oCcYg";
    private final static String SECRET = "0752dd7de8ec389877ad1280b9be06a6";
    private final static String ISSUER = "app-name";
    private final static long LEEWAY_SECONDS = 30L;
    private final static Duration DURATION = Duration.ofMinutes(15);

    public static String value(){
        return JWT_VALUE;
    }

    public static String secret(){
        return SECRET;
    }

    public static String issuer(){
        return ISSUER;
    }

    public static long leewaySeconds(){
        return LEEWAY_SECONDS;
    }

    public static Duration duration(){
        return DURATION;
    }

}
