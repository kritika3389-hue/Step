package week2;

import java.util.*;
import java.time.*;

public class FraudDetector {

    static class Transaction {
        int id;
        double amount;
        String merchant;
        String account;
        LocalDateTime timestamp;

        Transaction(int id, double amount, String merchant, String account, String time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.timestamp = LocalDateTime.parse("2026-03-11T" + time);
        }

        @Override
        public String toString() { return "ID:" + id + " ($" + amount + ")"; }
    }

    private final List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    /**
     * Classic Two-Sum variant for Fraud Detection.
     * Finds pairs that sum to target within a 1-hour window.
     */
    public List<String> findFraudulentPairs(double targetAmount) {
        List<String> suspiciousPairs = new ArrayList<>();
        Map<Double, Transaction> complementMap = new HashMap<>();

        for (Transaction current : transactions) {
            double complement = targetAmount - current.amount;

            if (complementMap.containsKey(complement)) {
                Transaction previous = complementMap.get(complement);
                Duration duration = Duration.between(previous.timestamp, current.timestamp);
                if (Math.abs(duration.toMinutes()) <= 60) {
                    suspiciousPairs.add("[" + previous + ", " + current + "]");
                }
            }
            complementMap.put(current.amount, current);
        }
        return suspiciousPairs;
    }

    /**
     * Detects duplicates: Same amount and merchant, different accounts.
     */
    public void detectDuplicatePayments() {
        Map<String, List<Transaction>> merchantMap = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            merchantMap.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        System.out.println("\nDuplicate Detection (Potential Multi-Account Fraud):");
        merchantMap.forEach((key, list) -> {
            if (list.size() > 1) {
                Set<String> accounts = new HashSet<>();
                list.forEach(t -> accounts.add(t.account));
                if (accounts.size() > 1) {
                    System.out.println("Alert: Same payment to " + key.split("\\|")[1] + " across accounts: " + accounts);
                }
            }
        });
    }

    static void main(String[] args) {
        FraudDetector detector = new FraudDetector();

        detector.addTransaction(new Transaction(1, 500, "Store A", "ACC-1", "10:00"));
        detector.addTransaction(new Transaction(2, 300, "Store B", "ACC-2", "10:15"));
        detector.addTransaction(new Transaction(3, 200, "Store C", "ACC-3", "10:30"));
        detector.addTransaction(new Transaction(4, 500, "Store A", "ACC-9", "10:45"));

        System.out.println("Suspicious pairs summing to $500:");
        detector.findFraudulentPairs(500).forEach(System.out::println);

        detector.detectDuplicatePayments();
    }
}
