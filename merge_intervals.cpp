#include <vector>
#include <algorithm>
using namespace std;

class Solution {
public:
    vector<vector<int>> merge(vector<vector<int>>& intervals) {
        vector<vector<int>> res;  
        int n = intervals.size();
        if (n == 0) return res;

        sort(intervals.begin(), intervals.end());

        int i = 0;
        while (i < n) {
            // Start of current merged interval
            vector<int> newinterval(2);
            newinterval[0] = intervals[i][0];
            newinterval[1] = intervals[i][1];

            //  Step 2: Merge overlapping intervals using min & max
            while (i < n - 1 && newinterval[1] >= intervals[i + 1][0]) {
                
                newinterval[0] = min(newinterval[0], intervals[i + 1][0]);
            
                newinterval[1] = max(newinterval[1], intervals[i + 1][1]);
                i++;
            }

            res.push_back(newinterval);

            i++;
        }

        return res;
    }
};
