package Solution;

public class UtilRandom {
    private static int base = 3;

    public static int getRandomVal(){
        int result = base * 2;
        base = base + (int)(Math.random() * 100);
        return result;
    }

    public static void reset(){
        base = 3;
    }
}
