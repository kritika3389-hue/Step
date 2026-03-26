package week2;

import java.util.*;

public class AutocompleteSystem {
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        List<String> topSuggestions = new ArrayList<>();
    }

    private final TrieNode root;
    private final Map<String, Integer> queryFrequency;
    private final int MAX_SUGGESTIONS = 10;

    public AutocompleteSystem() {
        root = new TrieNode();
        queryFrequency = new HashMap<>();
    }

    /**
     * Updates the frequency of a query and refreshes the Trie paths.
     */
    public void updateFrequency(String query) {
        queryFrequency.put(query, queryFrequency.getOrDefault(query, 0) + 1);
        insertIntoTrie(query);
    }

    private void insertIntoTrie(String query) {
        TrieNode curr = root;
        for (char c : query.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
            updateNodeSuggestions(curr, query);
        }
    }

    /**
     * Maintains the top 10 list at each node.
     */
    private void updateNodeSuggestions(TrieNode node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.add(query);
        }

        node.topSuggestions.sort((a, b) -> {
            int freqA = queryFrequency.get(a);
            int freqB = queryFrequency.get(b);
            return freqA != freqB ? freqB - freqA : a.compareTo(b);
        });

        if (node.topSuggestions.size() > MAX_SUGGESTIONS) {
            node.topSuggestions.remove(node.topSuggestions.size() - 1);
        }
    }

    /**
     * Returns top suggestions for a prefix in O(Length of Prefix) time.
     */
    public List<String> search(String prefix) {
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            if (!curr.children.containsKey(c)) {
                return Collections.emptyList();
            }
            curr = curr.children.get(c);
        }
        return curr.topSuggestions;
    }

    static void main(String[] args) {
        AutocompleteSystem scanner = new AutocompleteSystem();

        scanner.updateFrequency("java tutorial");
        scanner.updateFrequency("java tutorial"); // Rank it higher
        scanner.updateFrequency("javascript");
        scanner.updateFrequency("java download");
        scanner.updateFrequency("java 21 features");

        System.out.println("Suggestions for 'jav':");
        List<String> results = scanner.search("jav");
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }
    }
}
