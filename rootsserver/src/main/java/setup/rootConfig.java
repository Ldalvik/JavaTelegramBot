package setup;

import org.telegram.telegrambots.api.objects.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import static setup.helper.*;

/**
 * Created by subar on 5/30/2017.
 */

public class rootConfig extends TelegramLongPollingBot {
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            /* ROOTS CODE THY DOES START HERE NOW??!!1!?*/

            starts(update, "/info", "Hard work and java has created this bot. It is run off intellij locally for testing until final release.");
            starts(update, "/forward", "Reply to any message with /fwd @GroupName, @GroupName obviously being the group you want to send it to. The bot must be in the group, and for channels the bot must be admin. You can also send your own messages. Simply type '/fwd @GroupName //YourMessage'. If you type '/fwd @GroupName anon//YourMessage', it will only send 'YourMessage' to the chat without your username. Images are also supported.");
            starts(update, "/commands", "/info\n/score\n/add\n/text [text]\n/rand\n/msg\n/ping\n/hack (devs only)\n/forward\n/fwd\n/ig [instagram name]\n/kick (devs only)");
            contains(update, "epsilon?", "yes?");
            contains(update, "epsilon!", "WHAT?!!");

            forward(update);
            add(update);
            score(update);
            hack(update);
            //RandomMessages(update);
            random(update);
            instagram(update);
            ping(update);
            paint(update);
            //relay(update, "-1001102732445", "-1001074369635");
            summonAdmins(update);//relayAudio(update, "-1001102732445", "-1001074369635"); NOT WORKING
            kick(update);
            consolePic(update);
            log(update);

        }
    }


    @Override public String getBotUsername() {
        return BotConfig.user;
    }
    @Override public String getBotToken() {return BotConfig.token;}
}











