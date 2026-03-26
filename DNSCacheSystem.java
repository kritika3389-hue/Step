import java.util.*;
import java.util.concurrent.*;

public class DNSCacheSystem {
    // Custom Entry class to store IP and timing info
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
        // Background thread: Runs every 5 seconds to remove expired entries
        ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor();
        cleaner.scheduleAtFixedRate(this::cleanupExpired, 5, 5, TimeUnit.SECONDS);
    }

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits.incrementAndGet();
            return entry.ipAddress + " (Cache HIT)";
        }

        // Handle MISS or EXPIRED
        misses.incrementAndGet();
        String ip = queryUpstreamDNS(domain);
        
        // Simulating a 300s TTL for new entries
        cache.put(domain, new DNSEntry(ip, 300));
        return ip + " (Cache MISS/EXPIRED - Querying Upstream)";
    }

    private String queryUpstreamDNS(String domain) {
        // Simulating a real DNS query (e.g., to 8.8.8.8)
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

    public static void main(String[] args) throws InterruptedException {
        DNSCacheSystem dns = new DNSCacheSystem();

        // First resolution (Miss)
        System.out.println(dns.resolve("google.com"));
        
        // Second resolution (Hit)
        System.out.println(dns.resolve("google.com"));

        // Simulate a very short TTL for demonstration
        dns.cache.put("fast-expire.com", new DNSEntry("1.1.1.1", 1));
        System.out.println("Wait 2 seconds for expiration...");
        Thread.sleep(2000);
        
        System.out.println(dns.resolve("fast-expire.com")); // Should be Expired/Miss
        dns.getCacheStats();
    }
}
