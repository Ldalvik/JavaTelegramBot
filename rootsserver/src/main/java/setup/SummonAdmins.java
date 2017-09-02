package methods;

import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import setup.CreateCommand;
import setup.RootConfig;

import java.util.StringJoiner;

import static setup.Helper.send;
import static setup.Utils.removeTillWord;

public class SummonAdmins implements CreateCommand {

    private static RootConfig rf = new RootConfig();
    private static String messageText;
    private static String username;
    private static Long chatId;

    public void getVars(String _messageText, String _username, Long _chatId) {
        messageText = _messageText;
        username = _username;
        chatId = _chatId;
    }

    static void SummonAdmins() {
        GetChatAdministrators adminList = new GetChatAdministrators().setChatId(chatId);
            String summoned = removeTillWord(messageText, " ");
            StringJoiner joiner = new StringJoiner(", @");
            try {
                for (int i = 0; i < rf.getChatAdministrators(adminList).size(); i++) {
                    String adminIterator = rf.getChatAdministrators(adminList).listIterator(i).next().getUser().getUserName();
                    joiner.add(adminIterator);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            String allAdmins = joiner.toString();
            send("@" + allAdmins + ": " + summoned);
    }

    @Override
    public void StartCommand() {
        SummonAdmins();
    }
}
