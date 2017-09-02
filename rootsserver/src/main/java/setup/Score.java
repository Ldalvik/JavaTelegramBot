package methods;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import setup.CreateCommand;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static setup.Helper.error;
import static setup.Helper.send;
import static setup.Utils.delete_to;
import static setup.Vars.*;

public class Score implements CreateCommand{

    private static String messageText;
    private static String username;

    public void getVars(String _messageText, String _username) {
        messageText = _messageText;
        username = _username;
    }

    public void init() {
        try {
            add();
            score();
            hack();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    static void add() throws IOException, ParseException {
        if (messageText.startsWith("/add")) {
            File f = new File(dir + username + ".json");
            if (f.exists() && !f.isDirectory()) {
                JSONObject jsonObject = Parser();
                JSONObject jObj = new JSONObject();
                Long newscore = (Long) jsonObject.get(username) + 1;
                jObj.put(username, newscore);
                Parse(jObj);
                send("You won! Score: " + newscore);
            } else {
                JSONObject jObj = new JSONObject();
                jObj.put(username, 1);
                Parse(jObj);
                send("(First point!) You won! Score: 1");
            }
        }
    }

    static void score() {
        JSONParser parser = new JSONParser();
        if (messageText.startsWith("/score")) {
            try {
                Object obj = parser.parse(new FileReader(dir + username + ".json"));
                JSONObject jsonObject = (JSONObject) obj;
                Long userscore = (Long) jsonObject.get(username);
                send("Your score is: " + userscore);
            } catch (ParseException | IOException e) {
                error(String.valueOf(e));
            }
        }
    }

    static void hack() {
        if (messageText.startsWith("/hack")) {
            if (username.equals(dev) || username.equals(dev2) || username.equals(dev3)) {
                try {
                    JSONObject jsonObject = Parser();
                    JSONObject jObj = new JSONObject();
                    Long newscore = Long.valueOf(delete_to(messageText, " ") + jsonObject.get(username));
                    jObj.put(username, newscore);
                    Parse(jObj);
                    send("It's been hacked! Their score is now: " + newscore);
                } catch (ParseException | IOException e) {
                    error(String.valueOf(e));
                }
            } else {
                send("Sorry but you just can't hack it! Only cool people can...");
            }
        }
    }

    private static JSONObject Parser() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(dir + username + ".json"));
        JSONObject jsonObject = (JSONObject) obj;
        return jsonObject;
    }

    private static void Parse(JSONObject obj) {
        try (FileWriter file = new FileWriter(dir + username + ".json")) {
            file.write(obj.toJSONString());
            file.flush();
        } catch (IOException e) {
            error(String.valueOf(e));
        }
    }

    @Override
    public void StartCommand() {
        init();
    }
}
