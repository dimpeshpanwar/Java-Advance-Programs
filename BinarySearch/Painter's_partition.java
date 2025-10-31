import java.util.*;

class Solution {
    
    // Helper function to count how many painters are needed for a given time limit
    int countPainter(int[] arr, int timeLimit) {
        int totalTime = 0;
        int painters = 1;
        
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] + totalTime <= timeLimit) {
                totalTime += arr[i];
            } else {
                painters++;
                totalTime = arr[i];
            }
        }
        return painters;
    }

    // Main function to find minimum time
    int minTime(int[] arr, int k) {
        int low = Integer.MIN_VALUE;
        int high = 0;
        
        // Find the largest board and total sum
        for (int i = 0; i < arr.length; i++) {
            low = Math.max(low, arr[i]);
            high += arr[i];
        }
        
        // Binary search
        while (low <= high) {
            int mid = low + (high - low) / 2;
            
            int painters = countPainter(arr, mid);
            
            if (painters <= k) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        
        return low; // minimum possible time
    }
}
