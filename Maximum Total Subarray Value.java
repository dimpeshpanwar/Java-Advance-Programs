/*
 * 3689. Maximum Total Subarray Value I
 *
 * Description:
 * You are given an integer array 'nums' of length n and an integer 'k'.
 * You need to choose exactly k non-empty subarrays nums[l..r].
 * Subarrays may overlap, and the same subarray can be chosen more than once.
 *
 * The value of a subarray nums[l..r] = max(nums[l..r]) - min(nums[l..r])
 * The total value is the sum of all chosen subarray values.
 *
 * Goal: Return the maximum possible total value.
 *
 * Approach:
 * - To maximize the total value, we should maximize (max(nums[l..r]) - min(nums[l..r])).
 * - The highest difference in the entire array is (max(nums) - min(nums)).
 * - Since we can choose the same subarray multiple times (as per the problem),
 *   the best strategy is to choose the full array each time.
 * - Therefore, the maximum possible total value = k * (max(nums) - min(nums)).
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */

import java.util.*;

class Solution {
    public long maxTotalSubarrayValue(int[] nums, int k) {
        int maxVal = Integer.MIN_VALUE;
        int minVal = Integer.MAX_VALUE;

        for (int num : nums) {
            maxVal = Math.max(maxVal, num);
            minVal = Math.min(minVal, num);
        }

        long diff = (long) maxVal - (long) minVal;
        return diff * k;
    }
}

public class MaxTotalSubarrayValue {
    public static void main(String[] args) {
        Solution sol = new Solution();

        // Test Case 1
        System.out.println("Test Case 1:");
        int[] nums1 = {1, 3, 2};
        int k1 = 2;
        System.out.println("Nums: " + Arrays.toString(nums1));
        System.out.println("K: " + k1);
        System.out.println("Maximum Total Value: " + sol.maxTotalSubarrayValue(nums1, k1));
        System.out.println();

        // Test Case 2
        System.out.println("Test Case 2:");
        int[] nums2 = {4, 2, 5, 1};
        int k2 = 3;
        System.out.println("Nums: " + Arrays.toString(nums2));
        System.out.println("K: " + k2);
        System.out.println("Maximum Total Value: " + sol.maxTotalSubarrayValue(nums2, k2));
        System.out.println();

        // Test Case 3 (All elements same)
        System.out.println("Test Case 3:");
        int[] nums3 = {7, 7, 7};
        int k3 = 5;
        System.out.println("Nums: " + Arrays.toString(nums3));
        System.out.println("K: " + k3);
        System.out.println("Maximum Total Value: " + sol.maxTotalSubarrayValue(nums3, k3));
        System.out.println();

        // Test Case 4 (Single element)
        System.out.println("Test Case 4:");
        int[] nums4 = {10};
        int k4 = 4;
        System.out.println("Nums: " + Arrays.toString(nums4));
        System.out.println("K: " + k4);
        System.out.println("Maximum Total Value: " + sol.maxTotalSubarrayValue(nums4, k4));
    }
}
