package setup;

import methods.Forward;
import methods.Paint;
import methods.Roll;
import methods.Score;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import xboxapi.Endpoints;
import xboxapi.Gamercard;
import xboxapi.Presence;
import xboxapi.Profile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import static setup.Helper.*;
import static setup.Utils.*;
import static setup.Vars.*;
import static xboxapi.Root.Setup.Setup.isOnline;

public class Commands {

    private static RootConfig rf = new RootConfig();
    private static String messageText;
    private static String username;
    private static Long chatId;

    void getVars(String _messageText, String _username, Long _chatId) {
        messageText = _messageText;
        username = _username;
        chatId = _chatId;
    }

    public static void startCommand(String text, CreateCommand cmd) {
        if(messageText.startsWith(text)) {
            cmd.StartCommand();
        } else {

        }
    }

    static void Kick() {
        if (messageText.startsWith("/kick")) {
            if (username.equals(dev)) {
                LeaveChat lc = new LeaveChat().setChatId(chatId);
                try {
                    rf.leaveChat(lc);
                } catch (TelegramApiException e) {
                    error(String.valueOf(e));
                }
            }
        }
    }

    static void Instagram() {
        if (messageText.startsWith("/ig")) {
            String username = delete_to(messageText, " ");
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
                send(account);
                send(post);
            } catch (ParseException e) {
                e.printStackTrace();
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

    static void Ping() {
        if (messageText.startsWith("/ping")) {
            long milliStart = System.currentTimeMillis();
            pinging();
            long milliEnd = System.currentTimeMillis();
            long pong = milliEnd - milliStart;
            send("pong: " + pong+"ms");
        }
    }

    static void Xbox() {
        if (messageText.startsWith("/xbox ")) {
            Endpoints root = new Endpoints();

            String xuid = root.XUID(delete_to(messageText, " "));

            String profileJson = root.Profile(xuid);
            String gamercardJson = root.Gamercard(xuid);
            String presenceJson = root.Presence(xuid);
            Profile profileInfo = new Profile();
            String allProfile = profileInfo.All(profileJson);

            Gamercard gamercardInfo = new Gamercard();
            String allGamercard = gamercardInfo.All(gamercardJson);

            Presence presenceInfo = new Presence();
            Boolean isOnline = isOnline(presenceJson);
            String allPresence = presenceInfo.All(presenceJson, isOnline);

            send(allGamercard);
            send(allPresence);
            send(allProfile);
        }
    }
}
