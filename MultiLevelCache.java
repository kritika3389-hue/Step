import java.util.*;

public class MultiLevelCache {
    
    // Video Metadata Class
    static class Video {
        String id, title;
        Video(String id, String title) { this.id = id; this.title = title; }
    }

    // LRU Cache Implementation using LinkedHashMap
    class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;
        public LRUCache(int capacity) {
            super(capacity, 0.75f, true); // true = access-order
            this.capacity = capacity;
        }
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private final LRUCache<String, Video> l1Memory; // Fast RAM
    private final LRUCache<String, Video> l2SSD;    // Slower SSD
    private final Map<String, Integer> accessFrequency = new HashMap<>();
    
    // Metrics
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    public MultiLevelCache() {
        this.l1Memory = new LRUCache<>(10000); 
        this.l2SSD = new LRUCache<>(100000);
    }

    /**
     * Retrieves video through the cache hierarchy.
     */
    public Video getVideo(String videoId) {
        totalRequests++;
        
        // 1. Check L1 Cache
        if (l1Memory.containsKey(videoId)) {
            l1Hits++;
            System.out.println("→ L1 Cache HIT (0.5ms)");
            return l1Memory.get(videoId);
        }

        // 2. Check L2 Cache
        if (l2SSD.containsKey(videoId)) {
            l2Hits++;
            System.out.println("→ L1 Cache MISS (0.5ms) -> L2 Cache HIT (5ms)");
            Video v = l2SSD.get(videoId);
            
            // Access Pattern Promotion: Move to L1 if requested again
            l1Memory.put(videoId, v); 
            return v;
        }

        // 3. Check L3 (Simulated Database)
        l3Hits++;
        System.out.println("→ L1 MISS -> L2 MISS -> L3 Database HIT (150ms)");
        Video v = queryDatabase(videoId);
        
        // Add to L2 initially
        l2SSD.put(videoId, v);
        return v;
    }

    private Video queryDatabase(String id) {
        return new Video(id, "Movie Title " + id);
    }

    public void getStatistics() {
        System.out.println("\n--- Cache Performance Report ---");
        System.out.printf("L1 Hit Rate: %.1f%%%n", (l1Hits / (double) totalRequests) * 100);
        System.out.printf("L2 Hit Rate: %.1f%%%n", (l2Hits / (double) totalRequests) * 100);
        System.out.printf("L3 Hit Rate: %.1f%%%n", (l3Hits / (double) totalRequests) * 100);
        double avgTime = (l1Hits * 0.5 + l2Hits * 5.5 + l3Hits * 155.5) / totalRequests;
        System.out.printf("Avg Access Latency: %.2fms%n", avgTime);
        System.out.println("--------------------------------\n");
    }

    public static void main(String[] args) {
        MultiLevelCache netflix = new MultiLevelCache();

        // Simulate Requests
        netflix.getVideo("vid_1"); // L3 Hit
        netflix.getVideo("vid_1"); // L1 Hit (Promoted)
        netflix.getVideo("vid_2"); // L3 Hit
        netflix.getVideo("vid_1"); // L1 Hit
        
        netflix.getStatistics();
    }
}
