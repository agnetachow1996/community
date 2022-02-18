package com.nowcoder.community;

public class Solution {
    public int searchInsert(int[] nums, int target) {
        int low = 0; int high = nums.length;
        int mid = 0;
        while(low <= high){
            mid = (low + high) / 2;
            if(nums[mid] == target){
                return mid;
            }
            else if(nums[mid] > target){
                high = mid - 1;
            }else{
                low = mid + 1;
            }
        }
        return low;
    }

    public static void main(String[] args) {
        int[] a = {1,3,5,6};
        int b = 2;
        Solution s = new Solution();
        int result = s.searchInsert(a,b);
        System.out.println(result);
    }
}