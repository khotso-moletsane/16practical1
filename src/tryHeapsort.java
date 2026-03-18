import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class tryHeapsort {

    // ==================== INNER CLASS FOR TIMING RESULTS ====================
    public static class TimeResult {
        public long bottomUpTime;
        public long topDownTime;
        public boolean verified;

        public TimeResult(long but, long tdt, boolean v) {
            bottomUpTime = but;
            topDownTime = tdt;
            verified = v;
        }

        public void display() {
            System.out.println("\n--- TIMING RESULTS ---");
            System.out.printf("Bottom-up heapsort: %d ns (%.3f ms)%n",
                    bottomUpTime, bottomUpTime / 1_000_000.0);
            System.out.printf("Top-down heapsort:  %d ns (%.3f ms)%n",
                    topDownTime, topDownTime / 1_000_000.0);

            long diff = topDownTime - bottomUpTime;
            double ratio = (double) topDownTime / bottomUpTime;
            System.out.printf("\nDifference: %d ns (%.3f ms)%n", diff, diff / 1_000_000.0);
            System.out.printf("Top-down is %.2f times slower than bottom-up%n", ratio);
            System.out.println("Sorting verified: " + (verified ? "PASSED" : "FAILED"));
        }
    }

    // ==================== BOTTOM-UP HEAP METHODS ====================

    // Main heapsort method using bottom-up heap construction
    public static void heapSortBottomUp(String[] arr) {
        if (arr == null || arr.length <= 1) return;
        buildHeapBottomUp(arr);
        sortFromHeap(arr);
    }

    // Build heap from bottom up (Floyd's method) - O(n)
    private static void buildHeapBottomUp(String[] arr) {
        int n = arr.length;
        // Start from the last non-leaf node and work backwards
        for (int i = n / 2 - 1; i >= 0; i--) {
            sink(arr, i, n);
        }
    }

    // ==================== TOP-DOWN HEAP METHODS ====================

    // Main heapsort method using top-down heap construction
    public static void heapSortTopDown(String[] arr) {
        if (arr == null || arr.length <= 1) return;
        buildHeapTopDown(arr);
        sortFromHeap(arr);
    }

    // Build heap by inserting elements one by one - O(n log n)
    private static void buildHeapTopDown(String[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            swim(arr, i);
        }
    }

    // ==================== SHARED HEAP METHODS ====================

    // After heap is built, extract elements in sorted order
    private static void sortFromHeap(String[] arr) {
        int n = arr.length;
        // Extract elements one by one from the heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            swap(arr, 0, i);
            // Call sink on reduced heap
            sink(arr, 0, i);
        }
    }

    // Sink operation: move node at index i down to its correct position
    private static void sink(String[] arr, int i, int n) {
        while (true) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;

            // Compare with left child
            if (left < n && arr[left].compareTo(arr[largest]) > 0) {
                largest = left;
            }

            // Compare with right child
            if (right < n && arr[right].compareTo(arr[largest]) > 0) {
                largest = right;
            }

            // If largest is not the parent, swap and continue
            if (largest != i) {
                swap(arr, i, largest);
                i = largest;
            } else {
                break; // Node is in correct position
            }
        }
    }

    // Swim operation: move newly inserted element up to correct position
    private static void swim(String[] arr, int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (arr[i].compareTo(arr[parent]) <= 0) {
                break; // Already in correct position
            }
            swap(arr, i, parent);
            i = parent;
        }
    }

    // Helper method to swap two elements
    private static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // ==================== TESTING METHODS ====================

    // Test with small array to verify correctness
    public static void testWithSmallArray() {
        String[] testWords = {"dog", "cat", "bird", "fish", "ant",
                "zebra", "lion", "tiger", "bear", "wolf",
                "elephant", "giraffe", "monkey", "snake", "frog"};

        System.out.println("\n=== TESTING WITH SMALL ARRAY ===");
        System.out.println("Original (" + testWords.length + " words):");
        printWords(testWords, 5);

        // Test both methods
        String[] copy1 = testWords.clone();
        String[] copy2 = testWords.clone();

        // Time bottom-up
        long start1 = System.nanoTime();
        heapSortBottomUp(copy1);
        long end1 = System.nanoTime();
        long bottomUpTime = end1 - start1;

        // Time top-down
        long start2 = System.nanoTime();
        heapSortTopDown(copy2);
        long end2 = System.nanoTime();
        long topDownTime = end2 - start2;

        System.out.println("\nBottom-up sorted:");
        printWords(copy1, 5);
        System.out.println("Top-down sorted:");
        printWords(copy2, 5);

        System.out.printf("\nBottom-up time: %d ns%n", bottomUpTime);
        System.out.printf("Top-down time:  %d ns%n", topDownTime);

        // Verify both are sorted and identical
        boolean passed = true;
        for (int i = 0; i < copy1.length - 1; i++) {
            if (copy1[i].compareTo(copy1[i + 1]) > 0) {
                passed = false;
                break;
            }
        }
        for (int i = 0; i < copy1.length; i++) {
            if (!copy1[i].equals(copy2[i])) {
                passed = false;
                break;
            }
        }

        System.out.println("Test " + (passed ? "PASSED" : "FAILED"));
        System.out.println("================================\n");
    }

    // Helper to print words with line breaks
    private static void printWords(String[] words, int perLine) {
        for (int i = 0; i < words.length; i++) {
            System.out.print(words[i] + " ");
            if ((i + 1) % perLine == 0) System.out.println();
        }
        if (words.length % perLine != 0) System.out.println();
    }

    // ==================== TIMING METHODS ====================

    // Run timing comparison on the given word array
    public static TimeResult runFullSort(String[] words) {
        if (words == null || words.length == 0) {
            return new TimeResult(0, 0, false);
        }

        // Create copies to avoid modifying original
        String[] wordsCopy1 = words.clone();
        String[] wordsCopy2 = words.clone();

        System.out.println("\nRunning full heapsort on " + words.length + " words");
        System.out.println("==================================================");

        // Time bottom-up heapsort
        long start1 = System.nanoTime();
        heapSortBottomUp(wordsCopy1);
        long end1 = System.nanoTime();
        long bottomUpTime = end1 - start1;

        // Time top-down heapsort
        long start2 = System.nanoTime();
        heapSortTopDown(wordsCopy2);
        long end2 = System.nanoTime();
        long topDownTime = end2 - start2;

        // Verify both sorts produced the same result (should be sorted)
        boolean sortedCorrectly = true;
        for (int i = 0; i < wordsCopy1.length - 1; i++) {
            if (wordsCopy1[i].compareTo(wordsCopy1[i + 1]) > 0) {
                sortedCorrectly = false;
                System.out.println("ERROR: Bottom-up sort failed at position " + i +
                        ": " + wordsCopy1[i] + " > " + wordsCopy1[i + 1]);
                break;
            }
        }

        // Also verify both arrays are identical (both should be sorted)
        if (sortedCorrectly) {
            for (int i = 0; i < wordsCopy1.length; i++) {
                if (!wordsCopy1[i].equals(wordsCopy2[i])) {
                    sortedCorrectly = false;
                    System.out.println("ERROR: Sorts produced different results at position " + i);
                    break;
                }
            }
        }

        return new TimeResult(bottomUpTime, topDownTime, sortedCorrectly);
    }

    // ==================== FILE LOADING ====================

    // Load words from the cleaned Ulysses text file
    public static String[] loadWords(String filename) {
        ArrayList<String> wordList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                // Assuming the file has one word per line
                wordList.add(line.trim());
            }
            System.out.println("Loaded " + wordList.size() + " words from " + filename);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.out.println("Using default test array instead.");
            return null;
        }

        return wordList.toArray(new String[0]);
    }

    // Generate test words for when file is not available
    private static String[] generateTestWords(int count) {
        String[] words = new String[count];
        String[] prefixes = {"alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta", "iota", "kappa"};
        String[] suffixes = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};

        for (int i = 0; i < count; i++) {
            // Generate more realistic-looking words
            int p = (int)(Math.random() * prefixes.length);
            int s = (int)(Math.random() * suffixes.length);
            int num = (int)(Math.random() * 100);
            words[i] = prefixes[p] + suffixes[s] + num;
        }
        return words;
    }

    // ==================== MAIN METHOD ====================

    public static void main(String[] args) {
        System.out.println("=== HEAPSORT IMPLEMENTATION ===");
        System.out.println("Bottom-up vs Top-down comparison\n");

        // 1. First test with small array to verify correctness
        testWithSmallArray();

        // 2. Load the full word list from last week's cleaned file
        String[] words = loadWords("anagrams_output.tex"); // Adjust filename as needed

        // If file loading fails, create a larger test array
        if (words == null || words.length == 0) {
            System.out.println("\nCreating a larger test array (10000 words)...");
            words = generateTestWords(10000);
            System.out.println("Generated " + words.length + " test words");
        }

        // 3. Run full timing comparison
        if (words != null && words.length > 0) {
            TimeResult results = runFullSort(words);
            results.display();

            // Show first 10 words as sample
            System.out.println("\nSample of first 10 sorted words:");
            for (int i = 0; i < Math.min(10, words.length); i++) {
                System.out.print(words[i] + " ");
            }
            System.out.println("\n");
        }

        System.out.println("=== END OF PROGRAM ===");
    }
}