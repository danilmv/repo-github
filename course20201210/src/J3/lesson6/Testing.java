package J3.lesson6;

import java.util.Arrays;

public class Testing {
    public static final String ERROR_MSG_NO4 = "there is no 4 in the array";

    public static void main(String[] args) {
        Testing testing = new Testing();
        System.out.println(Arrays.toString(testing.getLast4Followers(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7})));
        System.out.println(testing.checkArrayHas1And4(new int[]{0, 1 , 2, 3, 5, 6}));
    }

    public int[] getLast4Followers(int[] array) {
        int last4 = -1;
        for (int i = 0; i < array.length; i++)
            if (array[i] == 4) last4 = i + 1;
        if (last4 == -1) throw new RuntimeException(ERROR_MSG_NO4);

        int[] result = new int[array.length - last4];
        for (int i = last4; i < array.length; i++) {
            result[i - last4] = array[i];
        }

        return result;
    }

    public boolean checkArrayHas1And4(int[] array) {
        boolean has1 = false;
        boolean has4 = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 1) has1 = true;
            if (array[i] == 4) has4 = true;
        }
        return has1 && has4;
    }
}
