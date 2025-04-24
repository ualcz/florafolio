package com.florafolio.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    @Value("${token.blacklist.expiration:604800}")
    private long blacklistExpiration;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

 
    public void blacklistToken(String token, UUID userId) {
        String tokenKey = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), blacklistExpiration, TimeUnit.SECONDS);
        
        // Associa o token com o usuário para revogação em massa
        String userTokensKey = USER_TOKENS_PREFIX + userId.toString();
        redisTemplate.opsForSet().add(userTokensKey, token);
        redisTemplate.expire(userTokensKey, blacklistExpiration, TimeUnit.SECONDS);
    }


    public boolean isTokenBlacklisted(String token) {
        String tokenKey = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    public void revokeAllUserTokens(UUID userId) {
        String userTokensKey = USER_TOKENS_PREFIX + userId.toString();
        
        // Obtém todos os tokens do usuário antes de excluir a chave
        Set<String> userTokens = redisTemplate.opsForSet().members(userTokensKey);
        
        // Verifica se a lista de tokens não é nula antes de iterá-la
        if (userTokens != null && !userTokens.isEmpty()) {
            // Revoga cada token do usuário
            userTokens.forEach(token -> {
                String tokenKey = TOKEN_BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(tokenKey, userId.toString(), blacklistExpiration, TimeUnit.SECONDS);
            });
        }
        
        // Agora remove a lista de tokens do usuário
        redisTemplate.delete(userTokensKey);
    }

}