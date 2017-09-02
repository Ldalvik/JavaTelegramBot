package setup;

import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

import static setup.Vars.*;


/**
 * Created by subar on 5/30/2017.
 */

public class Helper {

    private static RootConfig rf = new RootConfig();
    private static String messageText;
    private static Message message;
    private static String username;
    private static String firstName;
    private static String lastName;
    private static Long chatId;
    private static boolean isReply;
    private static String chatTitle;
    private static String rMessageText;
    private static Message rMessage;
    private static String rUsername;
    private static String rFirstName;
    private static String rLastName;
    private static int unixTime;
    private static boolean hasPic;

    void r_getVars(String _messageText, Message _message, String _username, String _firstName, String _lastName, Long _chatId, Boolean _isReply, String _chatTitle, String _rMessageText, Message _rMessage, String _rUsername, String _rFirstName, String _rLastName, int _unixTime, boolean _hasPic) {
        messageText = _messageText;
        message = _message;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
        chatId = _chatId;
        isReply = _isReply;
        chatTitle = _chatTitle;
        rMessageText = _rMessageText;
        rMessage = _rMessage;
        rUsername = _rUsername;
        rFirstName = _rFirstName;
        rLastName = _rLastName;
        unixTime = _unixTime;
        hasPic = _hasPic;
    }

    void getVars(String _messageText, Message _message, String _username, String _firstName, String _lastName, Long _chatId, Boolean _isReply, String _chatTitle, int _unixTime, boolean _hasPic) {
        messageText = _messageText;
        message = _message;
        username = _username;
        firstName = _firstName;
        lastName = _lastName;
        chatId = _chatId;
        isReply = _isReply;
        chatTitle = _chatTitle;
        unixTime = _unixTime;
        hasPic = _hasPic;
    }

    public static void contains(String sent, String response) {
        if (messageText.contains(sent)) {
            send(response);
        }
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);
        byte[] b = new byte[2048];
        int length;
        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }
        is.close();
        os.close();
    }

    public static String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);
        if (photo.hasFilePath()) {
            return photo.getFilePath();
        } else {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                org.telegram.telegrambots.api.objects.File file = rf.getFile(getFileMethod);
                return file.getFilePath();
            } catch (TelegramApiException e) {
                error(String.valueOf(e));
            }
        }
        return null;
    }

    public static PhotoSize getPhoto() {
        java.util.List<PhotoSize> photos = message.getReplyToMessage().getPhoto();
        return photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null);
    }

    public static void sendImagebyId(String photoId) {
        SendPhoto photo = new SendPhoto()
                .setChatId(chatId)
                .setPhoto(photoId);
        try {
            rf.sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    static void sendImage(String filePath, Long chatId) {
        SendPhoto photo = new SendPhoto()
                .setChatId(chatId)
                .setNewPhoto(new java.io.File(filePath));
        try {
            rf.sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendImage(String filePath, String chatId) {
        SendPhoto photo = new SendPhoto()
                .setChatId(chatId)
                .setNewPhoto(new java.io.File(filePath));
        try {
            rf.sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    static void sendImage(String filePath) {
        SendPhoto photo = new SendPhoto()
                .setChatId(chatId)
                .setNewPhoto(new java.io.File(filePath));
        try {
            rf.sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendAudio(String filePath) {
        SendAudio sendAudio = new SendAudio()
                .setChatId(chatId)
                .setNewAudio(new java.io.File(filePath));
        try {
            rf.sendAudio(sendAudio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void send(String msg) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(msg);
        try {
            rf.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    static void sendConsole(String chatid, String msg) {
        SendMessage message = new SendMessage()
                .setChatId(chatid)
                .setText(msg);
        try {
            rf.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void error(String response) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(response);
        try {
            rf.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static String url(String url) {
        try {
            URL urlObj = new URL(url);
            URLConnection con = urlObj.openConnection();
            con.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            final StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void equals(String sent, String response) {
        if (messageText.equals(sent)) {
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText(response);
            try {
                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(String.valueOf(e));
            }
        }
    }

    static void starts(String sent, String response) {
        if (messageText.startsWith(sent)) {
            SendMessage message = new SendMessage()
                    .setChatId(chatId)
                    .setText(response);
            try {
                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(String.valueOf(e));
            }
        }
    }

    static void consolePic() {
        if (messageText.equals("saveForRoot")) {
            PhotoSize img = getPhoto();
            String fileurl = getFilePath(img);
            try (InputStream in = new URL("https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl).openStream()) {
                Files.copy(in, Paths.get(dirUser(rUsername)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void log() {
        if (isReply) {
            System.out.println(rUsername + ": " + rMessageText + "\nREPLY:\n" + username + ": " + messageText + "\n--------------------------");
        } else {
            System.out.println("(" + chatId + ")" + username + ": " + messageText + "\n--------------------------");
        }
    }

    public static void relay(String group, String group2) {
        if (chatId.toString().equals(group) && isReply) {
            send(rUsername + ": " + rMessageText + "\n\nREPLY:\n\n" + username + ": " + messageText);
        } else {
            send(username + ": " + messageText);
        }
        if (chatId.toString().equals(group2) && isReply) {
            send(rUsername + ": " + rMessageText + "\n\nREPLY:\n\n" + username + ": " + messageText);
        } else {
            send(username + ": " + messageText);
        }
    }
}
