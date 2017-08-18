package setup;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.api.methods.send.SendAudio;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import static setup.Vars.*;
import static setup.utils.*;


/**
 * Created by subar on 5/30/2017.
 */
public class helper {

    private static final String LOGTAG = "channelSendError";
    private static final int WAITINGCHANNEL = 1;
    private static final String ERROR_MESSAGE_TEXT = "There was an error sending the message to channel *%s*, error: ```%s```";
    private static final ConcurrentHashMap<Integer, Integer> userState = new ConcurrentHashMap<>();

    static rootConfig rf = new rootConfig();

    public static void contains(Update update, String sent, String response) {
        String msg = msgText(update);
        if (msg.contains(sent)) {
              send(update, response);
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

    public static String getFilePath(Update update, PhotoSize photo) {
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
                error(update, String.valueOf(e));
            }
        }
        return null;
    }

    public static PhotoSize getPhoto(Update update) {
        java.util.List<PhotoSize> photos = update.getMessage().getReplyToMessage().getPhoto();
        return photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null);
    }

    public static void forward(Update update) {
        String username = username(update);
        String msg = msgText(update);
        Message message = message(update);
        if (msg.startsWith("/fwd")) {
            System.out.println(username + ": " + msg);
            String removeFirst = removeTillWord(msg, "@");
            String removeLast = removeFirst;
            String countString[] = removeLast.split(" ");
            String group = countString[0];
            //String lastTwo = words[words.length - 2] + " "
            //    + words[words.length - 1];
            System.out.println("--------\nThe group that it should be sending to is: " + group + "!\n--------");

                try {
                    handleIncomingMessage(update, message, group);
                } catch (InvalidObjectException e) {
                    BotLogger.severe(LOGTAG, e);
                } catch (Exception e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }

    private static void handleIncomingMessage(Update update, Message message, String group) throws InvalidObjectException {
        int state = userState.getOrDefault(message.getFrom().getId(), 0);
        switch (state) {
            default:
                sendMessageToChannel(update, message, group);
                userState.put(message.getFrom().getId(), WAITINGCHANNEL);
                break;
        }
    }

    private static void sendMessageToChannel(Update update, Message message, String group) {
        Message getmsg = message(update);
        String msg = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(group);
        if (getmsg.isReply() && getmsg.getReplyToMessage().getPhoto()!=null) {
            try {
                PhotoSize img = getPhoto(update);
                String fileurl = getFilePath(update, img);
                System.out.println("file id grabbed: " + fileurl);
                try (InputStream in = new URL("https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl).openStream()) {
                    String repliedUsername = r_username(update);
                    Files.copy(in, Paths.get(fwdPics(repliedUsername)));
                    sendImage(fwdPics(repliedUsername), group);
                    try {
                        File file = new File(fwdPics(repliedUsername));
                        if (file.delete()) {
                            System.out.println(file.getName() + " is deleted!");
                        } else {
                            System.out.println("Delete operation is failed.");
                        }
                    } catch (Exception e) {
                        error(update, String.valueOf(e));
                    }
                }
            } catch (IOException e) {
                error(update, String.valueOf(e));
            }
        } else {
            System.out.println("f exists else");
    }
        if (msg.contains("anon//") && !message.isReply()) {
            String sendmsganon = delete_to(msg, "anon//");
            sendMessage.setText(sendmsganon);
            try {
                rf.sendMessage(sendMessage);
            } catch (TelegramApiException e) {
                sendErrorMessage(message, e.getMessage());
                error(update, String.valueOf(e));
            }
        } else {
            System.out.println("contains anon// else");
        }
            if (message.getText().contains("msg//")) {
              String sendmsg = delete_to(msg, "msg//");
                sendMessage.setText("@" + message.getFrom().getUserName() + ": " + sendmsg);
                try {
                    rf.sendMessage(sendMessage);
                } catch (TelegramApiException e) {
                    sendErrorMessage(message, e.getMessage());
                    error(update, String.valueOf(e));
                }
            } else {
                System.out.println("contains msg// else");
            }

            if (message.isReply() && message.getReplyToMessage().getText()!=null) {
                sendMessage.setText("@" + message.getFrom().getUserName() + " forwarded message from @" + message.getReplyToMessage().getFrom().getUserName() + ": " + message.getReplyToMessage().getText());
                try {
                    rf.sendMessage(sendMessage);
                } catch (TelegramApiException e) {
                    sendErrorMessage(message, e.getMessage());
                    error(update, String.valueOf(e));
                }
            }
        }

    private static void sendErrorMessage(Message message, String errorText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(String.format(ERROR_MESSAGE_TEXT, message.getText().trim(), errorText));
        sendMessage.enableMarkdown(true);
        try {
            rf.sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);

        }
    }

    public static void sendImagebyId(String filePath, Long chatId) {
        SendPhoto photo = new SendPhoto()
        .setChatId(chatId)
        .setPhoto(filePath);
        try {
            rf.sendPhoto(photo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendImage(String filePath, Long chatId) {
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

    public static void sendAudio(String filePath, String chatId) {
        SendAudio sendAudio = new SendAudio()
                .setChatId(chatId)
                .setNewAudio(new java.io.File(filePath));
        try {
            rf.sendAudio(sendAudio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void send(Update update, String msg) {
        SendMessage message = new SendMessage()
                .setChatId(chat_id(update))
                .setText(msg);
        try {
            rf.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void sendConsole(String chat_id, String msg) {
        SendMessage message = new SendMessage()
                .setChatId(chat_id)
                .setText(msg);
        try {
            rf.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void error(Update update, String response) {
        SendMessage message = new SendMessage()
                .setChatId(chat_id(update))
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


    public static void add(Update update) {
        String msg = msgText(update);
        String username = username(update);
        String chatid = chat_title(update);

        try {
            if (msg.startsWith("/add")) {
                File f = new File(dir + username + ".json");
                if (f.exists() && !f.isDirectory()) {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new FileReader(dir + username + ".json"));
                    JSONObject jObj = new JSONObject();
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println(jsonObject);
                    Long userscore = (Long) jsonObject.get(username);
                    Long newscore = userscore + 1;
                    System.out.println(newscore);
                    jObj.put(username, newscore);

                    try (FileWriter file = new FileWriter(dir + username + ".json")) {
                        file.write(jObj.toJSONString());
                        file.flush();
                    } catch (IOException e) {
                        error(update, String.valueOf(e));
                    }
                    SendMessage message = new SendMessage()
                            .setChatId(chatid)
                            .setText("You won! Score: " + newscore);

                    try {
                        rf.sendMessage(message);
                    } catch (TelegramApiException e) {
                        error(update, String.valueOf(e));
                    }

                } else {
                    JSONObject jObj = new JSONObject();
                    jObj.put(username, 1);
                    try (FileWriter file = new FileWriter(dir + username + ".json")) {
                        file.write(jObj.toJSONString());
                        file.flush();
                        SendMessage message = new SendMessage()
                                .setChatId(chatid)
                                .setText("(First point!) You won! Score: 1");

                        try {
                            rf.sendMessage(message);
                        } catch (TelegramApiException e) {
                            error(update, String.valueOf(e));
                        }
                    } catch (IOException e) {
                        error(update, String.valueOf(e));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void score(Update update) {
        JSONParser parser = new JSONParser();
        String username = username(update);
        String msg = msgText(update);
        long chatid = chat_id(update);

        if (msg.startsWith("/score")) {

            try {
                Object obj = parser.parse(new FileReader(dir + username + ".json"));
                JSONObject jsonObject = (JSONObject) obj;
                System.out.println(jsonObject);
                Long userscore = (Long) jsonObject.get(username);
                SendMessage message = new SendMessage()
                        .setChatId(chatid)
                        .setText("Your score is: " + userscore);

                try {
                    rf.sendMessage(message);
                } catch (TelegramApiException e) {
                    error(update, String.valueOf(e));
                }
            } catch (org.json.simple.parser.ParseException e) {
                error(update, String.valueOf(e));
            } catch (FileNotFoundException e) {
                error(update, String.valueOf(e));
            } catch (IOException e) {
                error(update, String.valueOf(e));
            }
        }
    }

    public static void hack(Update update) {
        JSONParser parser = new JSONParser();
        String username = username(update);
        String msg = msgText(update);
        long chatid = chat_id(update);
        if (msg.startsWith("/hack")) {
            if (username.equals(dev) || username.equals(dev2) || username.equals(dev3)) {
                try {
                    Object obj = parser.parse(new FileReader(dir + username + ".json"));
                    JSONObject jObj = new JSONObject();
                    JSONObject jsonObject = (JSONObject) obj;
                    System.out.println(jsonObject);
                    Long userscore = (Long) jsonObject.get(username);
                    String newScoreStr = delete_to(msg, " ");
                    Long newScoreLong = Long.valueOf(newScoreStr);
                    Long newscore = userscore + newScoreLong;
                    System.out.println(newscore);
                    jObj.put(username, newscore);

                    try (FileWriter file = new FileWriter(dir + username + ".json")) {
                        file.write(jObj.toJSONString());
                        file.flush();
                    } catch (IOException e) {
                        error(update, String.valueOf(e));
                    }
                    send(update, "It's been hacked! Their score is now: " + newscore);
                } catch (ParseException e) {
                    error(update, String.valueOf(e));
                } catch (FileNotFoundException e) {
                    error(update, String.valueOf(e));
                } catch (IOException e) {
                    error(update, String.valueOf(e));
                }
            } else {
                send(update, "Sorry but you just can't hack it! Only cool people can...");
            }
        }
    }

    public static void kick(Update update) {
        String msg = msgText(update);
        String username = username(update);
        long chatid = chat_id(update);
        if (msg.startsWith("/kick")) {
            if (username.equals(dev)) {
                LeaveChat lc = new LeaveChat().setChatId(chatid);
                try {
                    rf.leaveChat(lc);
                } catch (TelegramApiException e) {
                    error(update, String.valueOf(e));
                }
            }
        }
    }

   /* public static void RandomMessages(Update update) {
        String msg = msgText(update);
        long chat_id = chat_id(update);
        String output = url("https://pastebin.com/raw/thelink");
        String[] word = output.split("---");
        int s = new Random().nextInt(word.length);
        String array = word[s];
        if (msg.startsWith("/msg")) {
            SendMessage message = new SendMessage()
                    .setChatId(chat_id)
                    .setText(array);

            try {
                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(update, String.valueOf(e));
            }
        }
    }*/

    public static void random(Update update) {
        String msg = msgText(update);

        if (msg.startsWith("/rand")) {
            String ran = String.valueOf((int) (Math.random() * (99999999 - 11111111) + 11111111));
            int[] eight = {11111111, 22222222, 33333333, 44444444, 55555555, 66666666, 77777777, 88888888, 99999999};
            int[] seven = {1111111, 2222222, 3333333, 4444444, 5555555, 6666666, 7777777, 8888888, 9999999};
            int[] six = {111111, 222222, 333333, 444444, 555555, 666666, 777777, 888888, 999999};
            int[] five = {11111, 22222, 33333, 44444, 55555, 66666, 77777, 88888, 99999};
            int[] four = {1111, 2222, 3333, 4444, 5555, 6666, 7777, 8888, 9999};
            int[] three = {111, 222, 333, 444, 555, 666, 777, 888, 999};
            int[] two = {11, 22, 33, 44, 44, 66, 77, 88, 99};
            int[] one = {1, 2, 3, 4, 5, 6, 7, 8, 9};

            for (int i = 0; i < 9; i++) {
                String v8 = String.valueOf(eight[i]);
                if (ran.endsWith(v8)) {
                    send(update, ran+"\nHOLY FUCKIN OCTUPLESSSSS");
                } else {
                    String v7 = String.valueOf(seven[i]);
                    if (ran.endsWith(v7)) {
                        send(update, ran+"\nOH MY YA GOT LUCKY FUCKIN SEVENSSS");
                    } else {
                        String v6 = String.valueOf(six[i]);
                        if (ran.endsWith(v6)) {
                            send(update, ran+"\nAYY yer gotta sextuples");
                        } else {
                            String v5 = String.valueOf(five[i]);
                            if (ran.endsWith(v5)) {
                                send(update, ran+"\nquintiples haha gg man");
                            } else {
                                String v4 = String.valueOf(four[i]);
                                if (ran.endsWith(v4)) {
                                    send(update, ran+"\nnice you got quaaadsss");
                                } else {
                                    String v3 = String.valueOf(three[i]);
                                    if (ran.endsWith(v3)) {
                                        send(update, ran+"\nooooh you got triples");
                                    } else {
                                        String v2 = String.valueOf(two[i]);
                                        if (ran.endsWith(v2)) {
                                            send(update, ran+"\nyou got doubles!");
                                        } else {
                                            String v1 = String.valueOf(one[i]);
                                            if (ran.endsWith(v1)) {
                                                send(update, ran+"\nyou got singles you are baad");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void pinging(){
        try {
          URL url = new URL("https://telegram.org");
          URLConnection conn = url.openConnection();
             BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 while (in.readLine() != null) {}
                       in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void ping(Update update) {
        String msg = msgText(update);
        long chatid = chat_id(update);

        if (msg.startsWith("/ping")) {
            long milliStart = System.currentTimeMillis();

            pinging();

            long milliEnd = System.currentTimeMillis();
            long pong = milliEnd - milliStart;

            SendMessage message = new SendMessage()
                    .setChatId(chatid)
                    .setText("pong: " + pong+"ms");
            try {

                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(update, String.valueOf(e));
            }
        }
    }

    public static void downloadFiles(Update update){




    }

    public static void equal(Update update, String sent, String response) {
        String msg = msgText(update);
        long chatid = chat_id(update);

        if (msg.equals(sent)) {
            SendMessage message = new SendMessage()
                    .setChatId(chatid)
                    .setText(response);
            try {
                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(update, String.valueOf(e));
            }
        }
    }

    public static void starts(Update update, String sent, String response) {
        String msg = msgText(update);
        long chatid = chat_id(update);

        if (msg.startsWith(sent)) {
            SendMessage message = new SendMessage()
                    .setChatId(chatid)
                    .setText(response);
            try {
                rf.sendMessage(message);
            } catch (TelegramApiException e) {
                error(update, String.valueOf(e));
            }
        }
    }


    public static void consolePic(Update update) {
            if(msgText(update).equals("saveForRoot")) {
                final BufferedImage image;
                    PhotoSize img = getPhoto(update);
                    String fileurl = getFilePath(update, img);
                    try (InputStream in = new URL("https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl).openStream()) {
                        String repliedUsername = r_username(update);
                        Files.copy(in, Paths.get(dirUser(repliedUsername)));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public static void paint(Update update) {
        String msg = msgText(update);
        long chatid = chat_id(update);
        String username = username(update);

        if (msg.startsWith("/text")) {
            //update.getMessage().getReplyToMessage().getPhoto();
            String result = removeTillWord(msg, " ");

            final BufferedImage image;
            try {
                PhotoSize img = getPhoto(update);
                String fileurl = getFilePath(update, img);
                image = ImageIO.read(new URL(
                        "https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl));
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setFont(new Font("TimesRoman", Font.PLAIN, 60));
                g.setColor(Color.RED);
                g.drawString(result, 100, 100);
                g.dispose();

                ImageIO.write(image, "jpg", new java.io.File(dir + username + ".jpg"));
                sendImage(dir + username + ".jpg", chatid);
            } catch (IOException e) {
                error(update, String.valueOf(e));
            }
        }
    }
    public static void log(Update update){
        String msg = msgText(update);
        String username = username(update);
        Long chatId = chat_id(update);

        if(isreply(update)) {
            String replymsg = r_msgText(update);
            String replyuser = r_username(update);
              System.out.println(replyuser + ": " + replymsg + "\nREPLY:\n" + username + ": " + msg+"\n--------------------------");
        } else {
            System.out.println("(" + chatId + ")" + username + ": " + msg+"\n--------------------------");
        }

    }

    public static void relay(Update update, String group, String group2) {
        String msg = msgText(update);
        String username = username(update);
        String replymsg = r_msgText(update);
        String replyuser = r_username(update);

        if (chat_id(update).toString().equals(group) && isreply(update)) {

            send(update, replyuser + ": " + replymsg + "\n\nREPLY:\n\n" + username + ": " + msg);
        } else {
            send(update, username + ": " + msg);
        }
        if (chat_id(update).toString().equals(group2) && isreply(update)) {
            send(update, replyuser + ": " + replymsg + "\n\nREPLY:\n\n" + username + ": " + msg);
        } else {
            send(update, username + ": " + msg);
        }
    }

    public static void audio(Update update) {
        String username = username(update);
            try {
                String fileurl = update.getMessage().getAudio().getFileId();
                System.out.println(fileurl);
                saveImage("https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl, dir + username + ".ogg");
                String audio = "https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl;
                //sendAudio(audio, group2);
            } catch (MalformedURLException e) {
                error(update, String.valueOf(e));
            } catch (IOException e) {
                error(update, String.valueOf(e));
            }
    }

    public static void instagram(Update update) {
        String msg = msgText(update);
        Long chatid = chat_id(update);

        if (msg.startsWith("/ig")) {

            String username = delete_to(msg, " ");
            String url = url("https://instagram.com/" + username);
            String first = delete_to(url, "{\"ProfilePage\": [");
            String last = deleteAfter(first, ", {\"__typename\":");
            String json = last+"]}}}";
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(json);

                JSONObject jsonObject = (JSONObject) obj;
                JSONObject user = (JSONObject) jsonObject.get("user");
                JSONObject following = (JSONObject) user.get("follows");
                JSONObject followers = (JSONObject) user.get("followed_by");

                Boolean isprivate = (Boolean) user.get("is_private");
                Long followingcount = (Long) following.get("count");
                String biography = (String) user.get("biography");
                Boolean verified = (Boolean) user.get("is_verified");
                String name = (String) user.get("full_name");
                String profilepic = (String) user.get("profile_pic_url_hd");
                Long followerscount = (Long) followers.get("count");
                String trimlikes = String.valueOf(user);
                String trimLikesBefore = delete_to(trimlikes, "\"likes\":");
                String trimLikesAfter = deleteAfter(trimLikesBefore, "}]}");

                Object likesObj = parser.parse(trimLikesAfter);
                JSONObject likesJson = (JSONObject) likesObj;

                Long likes = (Long) likesJson.get("count");

                String nodes = String.valueOf(user);
                String trimNodesBefore = delete_to(nodes, "\"nodes\":[");
                String trimNodesAfter = deleteAfter(trimNodesBefore, "]},");
                Object nodesObj = parser.parse(trimNodesAfter);
                JSONObject jsonObj = (JSONObject) nodesObj;

                JSONObject comments = (JSONObject) jsonObj.get("comments");

                String image = (String) jsonObj.get("display_src");
                Long commentscount = (Long) comments.get("count");
                String caption = (String) jsonObj.get("caption");
                Boolean video = (Boolean) jsonObj.get("is_video");

                String account = "Followers: " + followerscount + "\n" +
                        "Following: " + followingcount + "\n" +
                        "Bio: " + biography + "\n" +
                        "Name: " + name + "\n" +
                        "Verified: " + verified + "\n" +
                        "Private: " + isprivate + "\n" +
                        "Profile picture: " + profilepic;


                String post =  "---most recent post info---" + "\n" +
                        "Likes: " + likes + "\n" +
                        "Comments: " + commentscount + "\n" +
                        "Image: " + image + "\n" +
                        "Caption: " + caption + "\n" +
                        "Video: " + video;

                 send(update, account);
                 send(update, post);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }



    public static void summonAdmins(Update update) {
        String msg = msgText(update);
        Long chatid = chat_id(update);

        GetChatAdministrators adminList = new GetChatAdministrators().setChatId(chatid);
        if (msg.startsWith("/summon ")) {
            String summoned = removeTillWord(msg, " ");
            StringJoiner joiner = new StringJoiner(", @");
            try {
                for (int i = 0; i<rf.getChatAdministrators(adminList).size(); i++){
                    String adminIterator = rf.getChatAdministrators(adminList).listIterator(i).next().getUser().getUserName();
                    joiner.add(adminIterator);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            String allAdmins = joiner.toString();
            send(update, "@"+allAdmins + ": " + summoned);
            }
        }
    }
