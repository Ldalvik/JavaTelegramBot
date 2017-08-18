package setup;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Created by subar on 6/18/2017.
 */

public class Vars{
   public static final String dev = "katxbella";
   public static final String dev2 = "-";
   public static final String dev3 = "-";

    public static final String dir = "C:\\Users\\subar\\Desktop\\rootsserver\\";

    public static String dirUser(String username) {
        return "C:\\Users\\subar\\Desktop\\rootsserver\\saved\\" + username + ".jpg";
    }

    public static String fwdPics(String username) {
      return "C:\\Users\\subar\\Desktop\\rootsserver\\saved\\forwardedPictures\\" + username + ".jpg";

   }
   public static Long chat_id(Update update){
      return update.getMessage().getChat().getId();
   }
   public static String msgText(Update update){
      return update.getMessage().getText();
   }
   public static Message message(Update update){
      return update.getMessage();
   }
   public static String username(Update update){
      return update.getMessage().getFrom().getUserName();
   }
   public static String r_username(Update update){
      return update.getMessage().getReplyToMessage().getFrom().getUserName();
   }
   public static boolean isreply(Update update){
      return update.getMessage().isReply();
   }
   public static boolean haspic(Update update){
      return update.getMessage().hasPhoto();
   }
   public static boolean isPhotoNotNull(Update update){
        return update.getMessage().getPhoto()!=null;
   }
   public static boolean isPhotoNull(Update update){
        return update.getMessage().getPhoto()==null;
   }
   public static String chat_title(Update update){
      return update.getMessage().getChat().getTitle();
   }
   public static String firstname(Update update){
      return update.getMessage().getFrom().getFirstName();
   }
   public static String lastname(Update update){
      return update.getMessage().getFrom().getLastName();
   }
   public static int unix_time(Update update){
      return update.getMessage().getDate();
   }
   public static int r_unix_time(Update update){
      return update.getMessage().getReplyToMessage().getDate();
   }
   public static String r_msgText(Update update){
      return update.getMessage().getReplyToMessage().getText();
   }
   public static Message r_message(Update update){
      return update.getMessage().getReplyToMessage();
   }
    public static int r_chatid(Update update){
        return update.getMessage().getReplyToMessage().getFrom().getId();
    }
    public static String r_firstname(Update update){
        return update.getMessage().getReplyToMessage().getFrom().getFirstName();
    }
    public static String r_lastname(Update update){
        return update.getMessage().getReplyToMessage().getFrom().getLastName();
    }
   public static String pinned_msg_text(Update update){
      return update.getMessage().getPinnedMessage().getText();
   }
   public static String pinned_msg_user(Update update){
      return update.getMessage().getPinnedMessage().getFrom().getUserName();
   }
   public static String pinned_msg_firstname(Update update){
      return update.getMessage().getPinnedMessage().getFrom().getFirstName();
   }
   public static String pinned_msg_lastname(Update update){
      return update.getMessage().getPinnedMessage().getFrom().getLastName();
   }
}
