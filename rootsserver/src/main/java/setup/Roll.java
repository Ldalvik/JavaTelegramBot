package methods;

import org.telegram.telegrambots.api.objects.Message;

import static setup.Helper.*;

public class Roll {

    private static int[] eight;
    private static int[] seven;
    private static int[] six;
    private static int[] five;
    private static int[] four;
    private static int[] three;
    private static int[] two;
    private static int[] one;
    private static String[] message;
    private static String random;
    private static String messageText;

    public void getVars(String _messageText) {
        messageText = _messageText;
    }

    public void init() {
        if (messageText.startsWith("/rand")) {
            random = String.valueOf((int) (Math.random() * (99999999 - 11111111) + 11111111));
            message = new String[]{"Singles!", "Doubles!", "Triples!", "Quadruples!", "Quintuples!", "Sextuples!", "Septuples!", "Octuples!"};
            eight = new int[]{11111111, 22222222, 33333333, 44444444, 55555555, 66666666, 77777777, 88888888, 99999999};
            seven = new int[]{1111111, 2222222, 3333333, 4444444, 5555555, 6666666, 7777777, 8888888, 9999999};
            six = new int[]{111111, 222222, 333333, 444444, 555555, 666666, 777777, 888888, 999999};
            five = new int[]{11111, 22222, 33333, 44444, 55555, 66666, 77777, 88888, 99999};
            four = new int[]{1111, 2222, 3333, 4444, 5555, 6666, 7777, 8888, 9999};
            three = new int[]{111, 222, 333, 444, 555, 666, 777, 888, 999};
            two = new int[]{11, 22, 33, 44, 44, 66, 77, 88, 99};
            one = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
            for (int i = 0; i <= 8; i++) {
                int[] v = {eight[i], seven[i], six[i], five[i], four[i], four[i], three[i], two[i], one[i]};
                String number = String.valueOf(v[i]);
                if (random.endsWith(number)) {
                    send(random + "\n" + message[i]);
                }
            }
        }
    }
}
