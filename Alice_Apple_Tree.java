import java.util.Scanner;

public class Alice_Apple_Tree{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter the number of apples: ");
        int n = sc.nextInt();

        long level = 0;
        long sum = 0;
        for(; level <= 100000; level++){
            sum = 2 * level * (level + 1) * (2 * level + 1);
            if(sum >= n){
                break;
            }
        }
        System.out.println("The minimum perimeter of the grid is: " + 8 * level);
        sc.close();
    }
}