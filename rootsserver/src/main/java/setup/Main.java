package setup;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static setup.helper.*;
import static setup.utils.*;

/**
 * Created by subar on 5/30/2017.
 */

public class Main {
    public static void main(String[] args) {


        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            rootConfig rf = new rootConfig();
            botsApi.registerBot(rf);
            diceyfed();
        } catch (TelegramApiRequestException e1) {
            e1.printStackTrace();
        }
    }

    public static void console() {
        for (int i = 0; i < 999; i++) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Message: ");
                String msg = br.readLine();
                System.out.print("Chat id: ");
                String chat = br.readLine();
                System.out.println("Sending \"" + msg + "\" to \"@" + chat + "\"...");
                sendConsole("@" + chat, msg);
                System.out.println("Message sent succesfully!\n----------------------------------");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void diceyfed() {
        for (int i = 0; i < 999; i++) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String msg = br.readLine();

                if (msg.startsWith("file//")) {

                        String file = delete_to(msg, "file//");
                        sendImage(file, "@diceyfed");

                    } else {

                        sendConsole("@diceyfed", msg);
                        System.out.println("You: " + msg + "\n--------------------------");

                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
