# 🚀 High-Scale System Design Challenges

This repository contains Java-based solutions for 10 real-world system design problems. Each challenge focuses on high-performance data structures, $O(1)$ complexity, and handling millions of concurrent users.

## 🛠 Project Objectives
* **Performance:** Achieve sub-millisecond response times.
* **Scalability:** Design for datasets of 10M+ entries.
* **Concurrency:** Handle thousands of simultaneous requests safely.

---

## 📂 Problem Index

### 1. Social Media Username Checker
* **Scenario:** Registration system for 10M users.
* **Focus:** $O(1)$ lookups, attempt tracking, and suggestion logic.
* **Tech:** `HashMap`, Frequency Counting.

### 2. E-commerce Flash Sale Manager
* **Scenario:** 50,000 users buying 100 items simultaneously.
* **Focus:** Thread safety, preventing overselling, and FIFO waiting lists.
* **Tech:** `ConcurrentHashMap`, `LinkedHashMap`, Atomic Operations.

### 3. DNS Cache with TTL (Time To Live)
* **Scenario:** Reducing DNS lookup from 100ms to <1ms.
* **Focus:** Cache expiration, LRU eviction, and hit/miss ratios.
* **Tech:** Custom `Entry` class, Background cleanup threads.

### 4. Plagiarism Detection System
* **Scenario:** Checking student essays against 100,000 documents.
* **Focus:** Document fingerprinting and similarity indexing.
* **Tech:** N-Grams, String Hashing, Set intersections.

### 5. Real-Time Analytics Dashboard
* **Scenario:** 1M page views per hour with zero lag updates.
* **Focus:** Multi-dimensional frequency counting.
* **Tech:** `LinkedHashMap`, `PriorityQueue` for Top-N stats.

### 6. Distributed Rate Limiter
* **Scenario:** API Gateway for 100,000 clients (1k req/hr limit).
* **Focus:** Token Bucket algorithm and burst traffic handling.
* **Tech:** Sliding windows, Atomic counters.

### 7. Autocomplete Search System
* **Scenario:** 10M search queries with prefix matching.
* **Focus:** Low-latency (<50ms) suggestions.
* **Tech:** Trie + HashMap Hybrid, Min-Heaps.

### 8. Parking Lot Management
* **Scenario:** 500 spots with license plate tracking.
* **Focus:** Open addressing and collision resolution.
* **Tech:** Linear Probing, Custom Hash Functions.

### 9. Financial Fraud Detection
* **Scenario:** Identifying suspicious transaction pairs in real-time.
* **Focus:** Two-Sum variants and time-window filtering.
* **Tech:** Complement lookup via Hash Tables.

### 10. Multi-Level Cache System
* **Scenario:** Netflix-style tiered storage (L1 Memory, L2 SSD, L3 DB).
* **Focus:** Data promotion and cache invalidation.
* **Tech:** Tiered Hash Tables, LRU strategy.

---

## 📊 Concepts Summary
| Concept | Application |
| :--- | :--- |
| **Hash Tables** | Instant data retrieval |
| **LRU Eviction** | Memory management |
| **N-Grams** | Text similarity |
| **Token Bucket** | Traffic shaping/throttling |
| **Open Addressing** | Memory-efficient collision handling |
