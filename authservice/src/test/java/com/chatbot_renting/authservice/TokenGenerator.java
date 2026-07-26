package com.chatbot_renting.authservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TokenGenerator {
    public static void main(String[] args) {
        String secret = "WXw+FVujWxS1YAhmQt0W5FiId7LvI4IRfqPUG7rNhVdBya2PfiFa93EAw7Q06qB5cNKSrzjF2JTtzAB91kC1mA==";
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        
        String token = Jwts.builder()
                .setSubject("test@chatbot.com")
                .claim("roles", List.of("USER"))
                .claim("userId", UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();
                
        System.out.println("GENERATED_TOKEN=" + token);
    }
}
