package setup;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Created by subar on 6/18/2017.
 */

public class Vars {
    public static final String dev = "terrybawk";
    public static final String dev2 = "-";
    public static final String dev3 = "-";

    public static final String dir = "C:\\Users\\subar\\Desktop\\rootsserver\\";

    public static String dirUser(String username) {
        return "C:\\Users\\subar\\Desktop\\rootsserver\\saved\\" + username + ".jpg";
    }

    public static String fwdPics(String username) {
        return "C:\\Users\\subar\\Desktop\\rootsserver\\saved\\forwardedPictures\\" + username + ".jpg";

    }

    public static Long chat_id(Update update) {
        return update.getMessage().getChatId();
    }

    public static String msg_text(Update update) {
        return update.getMessage().getText();
    }

    public static Message message(Update update) {
        return update.getMessage();
    }

    public static String username(Update update) {
        return update.getMessage().getFrom().getUserName();
    }

    public static String r_username(Update update) {
        return update.getMessage().getReplyToMessage().getFrom().getUserName();
    }

    public static boolean is_reply(Update update) {
        return update.getMessage().isReply();
    }

    public static boolean has_pic(Update update) {
        return update.getMessage().hasPhoto();
    }

    public static boolean is_photo_null(Update update) {
        return update.getMessage().getPhoto() == null;
    }

    public static String chat_title(Update update) {
        return update.getMessage().getChat().getTitle();
    }

    public static String first_name(Update update) {
        return update.getMessage().getFrom().getFirstName();
    }

    public static String last_name(Update update) {
        return update.getMessage().getFrom().getLastName();
    }

    public static int unix_time(Update update) {
        return update.getMessage().getDate();
    }

    public static String r_msg_text(Update update) {
        return update.getMessage().getReplyToMessage().getText();
    }

    public static Message r_message(Update update) {
        return update.getMessage().getReplyToMessage();
    }

    public static String r_first_name(Update update) {
        return update.getMessage().getReplyToMessage().getFrom().getFirstName();
    }

    public static String r_last_name(Update update) {
        return update.getMessage().getReplyToMessage().getFrom().getLastName();
    }

    public static String pinned_msg_text(Update update) {
        return update.getMessage().getPinnedMessage().getText();
    }

    public static String pinned_msg_user(Update update) {
        return update.getMessage().getPinnedMessage().getFrom().getUserName();
    }

    public static String pinned_msg_first_name(Update update) {
        return update.getMessage().getPinnedMessage().getFrom().getFirstName();
    }

    public static String pinned_msg_last_name(Update update) {
        return update.getMessage().getPinnedMessage().getFrom().getLastName();
    }
}
