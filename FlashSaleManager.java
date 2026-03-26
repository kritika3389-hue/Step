import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleManager {
    // Thread-safe map for Product ID -> Stock Level
    private ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();
    
    // Thread-safe waiting list: Product ID -> Queue of User IDs (First-In, First-Out)
    private ConcurrentHashMap<String, ConcurrentLinkedQueue<Integer>> waitingLists = new ConcurrentHashMap<>();

    /**
     * Initialize a product in the catalog.
     */
    public void addProduct(String productId, int initialStock) {
        inventory.put(productId, new AtomicInteger(initialStock));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());
    }

    /**
     * Checks stock in O(1) time.
     */
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    /**
     * Attempts to purchase an item. 
     * Uses Atomic operations to prevent overselling.
     */
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Product not found.";

        // Attempt to decrement only if stock > 0
        // getAndUpdate ensures the check and decrement happen as one single "atomic" step
        int currentStock = stock.getAndUpdate(val -> val > 0 ? val - 1 : val);

        if (currentStock > 0) {
            return "Success! User " + userId + " purchased " + productId + ". Units remaining: " + (currentStock - 1);
        } else {
            // Add to waiting list if stock is empty
            waitingLists.get(productId).add(userId);
            int position = waitingLists.get(productId).size();
            return "Sold Out! User " + userId + " added to waiting list at position #" + position;
        }
    }

    public static void main(String[] args) {
        FlashSaleManager system = new FlashSaleManager();
        String product = "IPHONE15_256GB";
        
        // Initialize with only 2 units for testing
        system.addProduct(product, 2);

        System.out.println("Initial Stock: " + system.checkStock(product));

        // Simulate rapid-fire purchases
        System.out.println(system.purchaseItem(product, 12345)); // Success
        System.out.println(system.purchaseItem(product, 67890)); // Success
        System.out.println(system.purchaseItem(product, 99999)); // Waiting list
    }
}
