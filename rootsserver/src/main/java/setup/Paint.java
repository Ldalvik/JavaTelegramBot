package methods;

import org.telegram.telegrambots.api.objects.PhotoSize;
import setup.BotConfig;
import setup.CreateCommand;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static setup.Helper.*;
import static setup.Utils.removeTillWord;
import static setup.Vars.dir;

public class Paint implements CreateCommand {
    private static String color;
    private static int font;
    private static int x_coord;
    private static int y_coord;
    private static String text;
    private static int textSize;
    private static String messageText;
    private static String username;
    private static Long chatId;
    private static String result;
    private static String[] vars;
    private static BufferedImage image;

    public void getVars(String _messageText, String _username, Long _chatId) {
        messageText = _messageText;
        username = _username;
        chatId = _chatId;
    }

    private static void load() {
        result = removeTillWord(messageText, " ").trim();
        vars = result.split(", ");
        color = vars[0];
        font = Integer.parseInt(vars[1]);
        x_coord = Integer.parseInt(vars[2]);
        y_coord = Integer.parseInt(vars[3]);
        textSize = Integer.parseInt(vars[4]);
        text = vars[5];
        System.out.println("DEBUG ((PAINT.JAVA)):\n" + vars[0] + "\n" + vars[1] + "\n" + vars[2] + "\n" + vars[3] + "\n" + vars[4] + "\n" + vars[5]);
        try {
            PhotoSize img = getPhoto();
            String fileurl = getFilePath(img);
            image = ImageIO.read(new URL(
                    "https://api.telegram.org/file/bot" + BotConfig.token + "/" + fileurl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        if (messageText.startsWith("/text")) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            getColor(g);
            getFont(g);
            getText(g);
            g.dispose();
            try {
                ImageIO.write(image, "png", new java.io.File(dir + username + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendImage(dir + username + ".png", String.valueOf(chatId));
        }
    }

    private static void getColor(Graphics2D g) {
        Color(g, color, "red", Color.RED);
        Color(g, color, "orange", Color.ORANGE);
        Color(g, color, "yellow", Color.YELLOW);
        Color(g, color, "green", Color.GREEN);
        Color(g, color, "blue", Color.BLUE);
        Color(g, color, "black", Color.BLACK);
        Color(g, color, "cyan", Color.CYAN);
        Color(g, color, "gray", Color.GRAY);
        Color(g, color, "magenta", Color.MAGENTA);
        Color(g, color, "pink", Color.PINK);
        Color(g, color, "white", Color.WHITE);
    }

    private static void getFont(Graphics2D g) {
        Font(g, font, textSize);
        Font(g, font, textSize);
        Font(g, font, textSize);
    }

    private static void getText(Graphics2D g) {
        Text(g, text, x_coord, y_coord);
    }

    private static void Color(Graphics2D g, String msg, String colorString, Color color) {
        if (msg.equalsIgnoreCase(colorString)) {
            g.setColor(color);
        }
    }

    private static void Font(Graphics2D g, int fontType, int textSize) {
        g.setFont(new Font("TimesRoman", fontType, textSize));
    }

    private static void Text(Graphics2D g, String text, int x, int y) {
        g.drawString(text, x, y);
    }

    @Override
    public void StartCommand() {
        init();
    }
}
