package lesson1;

public class HomeWork1 {
    public static void main(String[] args){
        System.out.println("Результат первого метода: " + doCalc( 5, 3, 7, 9) );
        System.out.println("Результат второго метода: " + checkSum( 7, 3 ) );
    }
    public static float doCalc(int a, int b, int c, int d) {
        return a * (b + (c / (float)d));
    }

    public static boolean checkSum(int a, int b){
        int c = a + b;
        return c >= 10 && c <= 20;
    }
}
