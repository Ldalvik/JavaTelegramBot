package setup;

import methods.*;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import static setup.Commands.*;
import static setup.SetCommands.Start;
import static setup.Vars.*;
import static setup.Helper.*;

/**
 * Created by subar on 5/30/2017.
 */

public class RootConfig extends TelegramLongPollingBot {
    String rmsg;
    Message rmessage;
    String rusername;
    String rfirstname;
    String rlastname;
    public void onUpdateReceived(Update update) {
        /* ROOTS CODE THY DOES START HERE NOW??!!1!?*/

        Forward f = new Forward();
        Helper h = new Helper();
        Score s = new Score();
        Paint p = new Paint();
        Roll r = new Roll();
        Commands c = new Commands();
        SummonAdmins sa =  new SummonAdmins();

        String msg = msg_text(update);
        Message message = message(update);
        String username = username(update);
        String firstname = first_name(update);
        String lastname = last_name(update);
        Long chatid = chat_id(update);
        String chat_title = chat_title(update);
        int unixtime = unix_time(update);
        Boolean haspic = has_pic(update);
        boolean isreply = is_reply(update);

        if(isreply) {
            rmsg = r_msg_text(update);
            rmessage = r_message(update);
            rusername = r_username(update);
            rfirstname = r_first_name(update);
            rlastname = r_last_name(update);
        }

        if (isreply) {
            h.r_getVars(msg, message, username, firstname, lastname, chatid, isreply, chat_title, rmsg, rmessage, rusername, rfirstname, rlastname, unixtime, haspic);
        } else {
            h.getVars(msg, message, username, firstname, lastname, chatid, isreply, chat_title, unixtime, haspic);
        }
        if (isreply) {
            f.r_getVars(msg, message, username, chatid, isreply, rmsg, rusername);
        } else {
            f.getVars(msg, message, username, chatid, isreply);
        }
        if (!isreply) {
            c.getVars(msg, username, chatid);
        }
        if (!isreply) {
            sa.getVars(msg, username, chatid);
        }
        if (!isreply) {
            p.getVars(msg, username, chatid);
        }
        if (!isreply) {
            s.getVars(msg, username);
        }
        if(!isreply){
            r.getVars(msg);
        }

        contains("epsilon?", "yes?");
        contains("epsilon!", "WHAT?!!");
        contains("/niv", "biggest skid you will ever meet");

        starts("/info", "Hard work and java has created this bot. It is run off intellij locally for testing until final release.");
        starts("/forward", "Reply to any message with /fwd @GroupName, @GroupName obviously being the group you want to send it to. The bot must be in the group, and for channels the bot must be admin. You can also send your own messages. Simply type '/fwd @GroupName //YourMessage'. If you type '/fwd @GroupName anon//YourMessage', it will only send 'YourMessage' to the chat without your username. Images are also supported.");
        starts("/commands", "/xbox [gamertag]\n/ig [username]\n/info\n/score\n/add\n/text [color, font, x-coordinate, y-coordinate, text size, text you want on the pic]\n/rand\n/msg\n/ping\n/hack (devs only)\n/forward\n/fwd\n/kick (devs only)");
        starts("/texthelp", "Reply to an image with '/text' with these parameters: color, font, x-coordinate, y-coordinate, text size, text for pic.\nColors available:\n" +
                "red\n" +
                "orange\n" +
                "yellow\n" +
                "green\n" +
                "blue\n" +
                "black\n" +
                "cyan\n" +
                "gray\n" +
                "magenta\n" +
                "pink\n" +
                "white\n" +
                "Available fonts are:\n" +
                "0 (plain)\n" +
                "1 (bold)\n" +
                "2 (italic)\n" +
                "For coordinates, experiment (varies with picture size) start with 100, 100.\n" +
                "Default text size is 60, once again experiment.\n" +
                "Type whatever text you want to display on the picture.\n" +
                "Example command:\n" +
                "/text black, 2, 100, 100, 70, This text will be on the picture.");
        Start();
        //RandomMessages();
        Instagram();
        Ping();
        //relay("-1001102732445", "-1001074369635");
        Kick();
        consolePic();
        log();
        Xbox();
    }

    @Override
    public String getBotUsername() {
        return BotConfig.user;
    }

    @Override
    public String getBotToken() {
        return BotConfig.token;
    }
}










