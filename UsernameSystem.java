import java.util.*;

public class UsernameSystem {
    // Stores taken usernames: Map<Username, UserId>
    private Map<String, Integer> registeredUsers = new HashMap<>();
    
    // Tracks popularity: Map<Username, Number of attempts>
    private Map<String, Integer> attemptTracker = new HashMap<>();
    
    private int nextUserId = 1;

    /**
     * Checks if a username is available in O(1) time.
     */
    public boolean checkAvailability(String username) {
        // Track the attempt regardless of availability
        attemptTracker.put(username, attemptTracker.getOrDefault(username, 0) + 1);
        
        return !registeredUsers.containsKey(username.toLowerCase());
    }

    /**
     * Registers a user if the name is free.
     */
    public void registerUser(String username) {
        if (checkAvailability(username)) {
            registeredUsers.put(username.toLowerCase(), nextUserId++);
            System.out.println("Successfully registered: " + username);
        } else {
            System.out.println("Registration failed: Username taken.");
        }
    }

    /**
     * Suggests similar usernames by appending numbers or suffixes.
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;

        while (suggestions.size() < 3) {
            String candidate = username + suffix;
            if (!registeredUsers.containsKey(candidate.toLowerCase())) {
                suggestions.add(candidate);
            }
            suffix++;
        }
        return suggestions;
    }

    /**
     * Returns the username that people have tried to register most often.
     */
    public String getMostAttempted() {
        return attemptTracker.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No attempts yet");
    }

    public static void main(String[] args) {
        UsernameSystem system = new UsernameSystem();

        // Setup initial users
        system.registerUser("john_doe");

        // Test Availability
        System.out.println("Is 'john_doe' available? " + system.checkAvailability("john_doe")); 
        System.out.println("Is 'jane_smith' available? " + system.checkAvailability("jane_smith"));

        // Test Suggestions
        if (!system.checkAvailability("john_doe")) {
            System.out.println("Suggestions for 'john_doe': " + system.suggestAlternatives("john_doe"));
        }

        // Test Popularity
        system.checkAvailability("admin");
        system.checkAvailability("admin");
        System.out.println("Most attempted username: " + system.getMostAttempted());
    }
}
