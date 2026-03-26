package week1;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DNSCacheSystem {
    private static class DNSEntry {
        String ipAddress;
        long createdAt;
        long ttlMillis;

        DNSEntry(String ipAddress, int ttlSeconds) {
            this.ipAddress = ipAddress;
            this.createdAt = System.currentTimeMillis();
            this.ttlMillis = ttlSeconds * 1000L;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > ttlMillis;
        }
    }

    private final Map<String, DNSEntry> cache = new ConcurrentHashMap<>();
    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);

    public DNSCacheSystem() {
        ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
        cleaner.scheduleAtFixedRate(this::cleanupExpired, 5, 5, TimeUnit.SECONDS);
    }

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits.incrementAndGet();
            return entry.ipAddress + " (Cache HIT)";
        }

        misses.incrementAndGet();
        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(ip, 300));
        return ip + " (Cache MISS/EXPIRED - Querying Upstream)";
    }

    private String queryUpstreamDNS(String domain) {
        return "172.217.14." + (new Random().nextInt(254) + 1);
    }

    private void cleanupExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public void getCacheStats() {
        int total = hits.get() + misses.get();
        double rate = (total == 0) ? 0 : (hits.get() / (double) total) * 100;
        System.out.printf("Stats -> Total: %d, Hits: %d, Misses: %d, Hit Rate: %.2f%%%n", 
                          total, hits.get(), misses.get(), rate);
    }

     static void main(String[] args) throws InterruptedException {
        DNSCacheSystem dns = new DNSCacheSystem();

        System.out.println(dns.resolve("google.com"));

        System.out.println(dns.resolve("google.com"));

        dns.cache.put("fast-expire.com", new DNSEntry("1.1.1.1", 1));
        System.out.println("Wait 2 seconds for expiration...");
        Thread.sleep(2000);
        
        System.out.println(dns.resolve("fast-expire.com"));
        dns.getCacheStats();
    }
}
