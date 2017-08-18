package setup;

/**
 * Created by subar on 7/13/2017.
 */
public class utils {

    public static String removeTillWord(String input, String word) {
        return input.substring(input.indexOf(word));
    }

    public static String replace(String input, String word, String replace) {
        return input.replace(word, replace);
    }

    public static String delete_to(String msg, String trim){
        String trimStart = removeTillWord(msg, trim);
        String trimEnd = replace(trimStart, trim, "");
        return trimEnd;
    }

    public static String deleteAfter(String msg, String trim){
        return msg.substring(0, msg.indexOf(trim));
    }
}
