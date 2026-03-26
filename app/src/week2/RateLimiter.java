package week2;

import java.util.concurrent.*;
import java.util.*;

public class RateLimiter {
    private static class TokenBucket {
        private final long maxTokens;
        private final double refillRatePerMs;
        private double currentTokens;
        private long lastRefillTimestamp;

        public TokenBucket(long limitPerHour) {
            this.maxTokens = limitPerHour;
            this.refillRatePerMs = (double) limitPerHour / (3600 * 1000);
            this.currentTokens = limitPerHour;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (currentTokens >= 1.0) {
                currentTokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTimestamp;
            double tokensToAdd = timePassed * refillRatePerMs;
            
            currentTokens = Math.min(maxTokens, currentTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }

        public long getRemaining() {
            refill();
            return (long) currentTokens;
        }

        public long getNextResetSeconds() {
            return (long) ((maxTokens - currentTokens) / refillRatePerMs / 1000);
        }
    }

    private final Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();
    private final long LIMIT = 1000;

    /**
     * Checks if a client is within their rate limit.
     */
    public String checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket(LIMIT));

        if (bucket.tryConsume()) {
            return "Allowed (" + bucket.getRemaining() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining, retry after " + bucket.getNextResetSeconds() + "s)";
        }
    }

    static void main(String[] args) {
        RateLimiter limiter = new RateLimiter();
        String testClient = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + (i + 1) + ": " + limiter.checkRateLimit(testClient));
        }

        System.out.println("\n--- Simulating limit exhaustion ---");
        TokenBucket b = limiter.clientBuckets.get(testClient);
        b.currentTokens = 0; 
        
        System.out.println(limiter.checkRateLimit(testClient));
    }
}
