package methods;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import setup.BotConfig;
import setup.CreateCommand;
import setup.RootConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import static setup.Helper.*;
import static setup.Utils.delete_to;
import static setup.Utils.removeTillWord;
import static setup.Vars.fwdPics;

public class Forward implements CreateCommand{

    private static final String LOGTAG = "channelSendError";
    private static final int WAITINGCHANNEL = 1;
    private static final String ERROR_MESSAGE_TEXT = "There was an error sending the message to channel *%s*, error: ```%s```";
    private static final ConcurrentHashMap<Integer, Integer> userState = new ConcurrentHashMap<>();

    private static RootConfig rf = new RootConfig();
    private static String messageText;
    private static Message message;
    private static String username;
    private static Long chatId;
    private static boolean isReply;
    private static String rMessageText;
    private static String rUsername;
    private static String group;


    public void r_getVars(String _messageText, Message _message, String _username, Long _chatId, Boolean _isReply, String _rMessageText, String _rUsername) {
        messageText = _messageText;
        message = _message;
        username = _username;
        chatId = _chatId;
        isReply = _isReply;
        rMessageText = _rMessageText;
        rUsername = _rUsername;
    }

    public void getVars(String _messageText, Message _message, String _username, Long _chatId, Boolean _isReply) {
        messageText = _messageText;
        message = _message;
        username = _username;
        chatId = _chatId;
        isReply = _isReply;
    }

    public void init() {
            String removeLast = removeTillWord(messageText, "@");
            String countString[] = removeLast.split(" ");
            group = countString[0];

            ReplyPhoto();
            ReplyMessage();
            Anonymous();
            Message();
    }

    private static void ReplyPhoto() {
        if (message.isReply() && message.getReplyToMessage().getPhoto() != null) {
            try {
                PhotoSize img = getPhoto();
                String fileurl = getFilePath(img);
                try (InputStream in = new URL("https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl).openStream()) {
                    Files.copy(in, Paths.get(fwdPics(rUsername)));
                    sendImage(fwdPics(rUsername), group);
                    try {
                        File file = new File(fwdPics(rUsername));
                        if (file.delete()) {
                            System.out.println(file.getName() + " is deleted!");
                        } else {
                            System.out.println("Delete operation is failed.");
                        }
                    } catch (Exception e) {
                        error(String.valueOf(e));
                    }
                }
            } catch (IOException e) {
                error(String.valueOf(e));
            }
        }
    }

    private static void Anonymous() {
        if (messageText.contains("anon//") && !message.isReply()) {
            String message = delete_to(messageText, "anon//");
            SendMessage sendMessage = new SendMessage()
                    .enableMarkdown(true)
                    .setChatId(group)
                    .setText(message)
                    .setChatId(group);
            try {
                rf.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                sendErrorMessage(e.getMessage());
                error(String.valueOf(e));
            }
        }
    }

    private static void Message() {
        if (messageText.contains("msg//") && !message.isReply()) {
            String sendmsg = delete_to(messageText, "msg//");
            SendMessage sendMessage = new SendMessage()
                    .setText("@" + username + ": " + sendmsg)
                    .setChatId(group);
            try {
                rf.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                sendErrorMessage(e.getMessage());
                error(String.valueOf(e));
            }
        }
    }

    private static void ReplyMessage() {
        if (isReply && rMessageText != null) {
            SendMessage sendMessage = new SendMessage()
                    .setText("@" + rUsername + " forwarded message from @" + rUsername + ": " + rMessageText)
                    .setChatId(group);
            try {
                rf.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                sendErrorMessage(e.getMessage());
                error(String.valueOf(e));
            }
        }
    }

    private static void sendErrorMessage(String errorText) {
        SendMessage sendMessage = new SendMessage()
                .enableMarkdown(true)
                .setChatId(chatId)
                .setReplyToMessageId(message.getMessageId())
                .setText(String.format(ERROR_MESSAGE_TEXT, messageText.trim(), errorText))
                .enableMarkdown(true);
        try {
            rf.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }

    @Override
    public void StartCommand() {
        init();
    }
}
