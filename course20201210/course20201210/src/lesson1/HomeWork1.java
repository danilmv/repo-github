package lesson1;

public class HomeWork1 {
    public static void main(String[] args){
        System.out.println("Результат первого метода: " + doCalc( 5, 3, 7, 0) );
    }
    public static float doCalc(int a, int b, int c, int d) {
            return a * (b + (c / (float)d));
    }
}
