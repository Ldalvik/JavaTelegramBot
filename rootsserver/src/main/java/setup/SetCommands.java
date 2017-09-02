package setup;

import methods.Forward;
import methods.Paint;
import methods.Score;
import methods.SummonAdmins;

import static setup.Commands.startCommand;

public class SetCommands {

    public static void Start(){
        startCommand("/summon", new SummonAdmins());
        startCommand("/add", new Score());
        startCommand("/score", new Score());
        startCommand("/hack ", new Score());
        startCommand("/fwd ", new Forward());
        startCommand("/text ", new Paint());
    }
}
