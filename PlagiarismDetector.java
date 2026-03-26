import java.util.*;

public class PlagiarismDetector {
    // Maps an N-Gram (fingerprint) to a set of Document IDs that contain it
    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    
    // Stores the total N-Gram count for each document to calculate percentages
    private Map<String, Integer> docTotalNgrams = new HashMap<>();
    
    private final int N = 5; // Using 5-grams as per hints

    /**
     * Adds a document to the database by breaking it into N-Grams.
     */
    public void addDocument(String docId, String content) {
        List<String> ngrams = generateNgrams(content);
        docTotalNgrams.put(docId, ngrams.size());

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    /**
     * Analyzes a new document against the database to find similarities.
     */
    public void analyzeDocument(String content) {
        List<String> inputNgrams = generateNgrams(content);
        Map<String, Integer> matchCounts = new HashMap<>();

        // O(n) scan of the input document n-grams
        for (String gram : inputNgrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String existingDocId : ngramIndex.get(gram)) {
                    matchCounts.put(existingDocId, matchCounts.getOrDefault(existingDocId, 0) + 1);
                }
            }
        }

        System.out.println("Analysis Results:");
        matchCounts.forEach((docId, count) -> {
            double similarity = (count.doubleValue() / inputNgrams.size()) * 100;
            String status = similarity > 50 ? "PLAGIARISM DETECTED" : (similarity > 10 ? "SUSPICIOUS" : "CLEAN");
            System.out.printf("-> Found %d matching n-grams with %s | Similarity: %.1f%% (%s)%n", 
                              count, docId, similarity, status);
        });
    }

    /**
     * Helper to break text into sequences of N words (Sliding Window).
     */
    private List<String> generateNgrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<String> ngrams = new ArrayList<>();
        
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(j < N - 1 ? " " : "");
            }
            ngrams.add(sb.toString());
        }
        return ngrams;
    }

    public static void main(String[] args) {
        PlagiarismDetector detector = new PlagiarismDetector();

        // Setup database
        detector.addDocument("essay_089.txt", "the quick brown fox jumps over the lazy dog");
        detector.addDocument("essay_092.txt", "java is a high level class based object oriented programming language");

        // Test Input
        String newSubmission = "java is a high level class based programming language and more";
        System.out.println("Analyzing submission...");
        detector.analyzeDocument(newSubmission);
    }
}
