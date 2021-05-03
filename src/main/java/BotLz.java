import event.event;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class BotLz {
    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("ODM4NjgxMTk3NTUwODk1MTY0.YI-ouw.G39Njy1R1k0pvbmHMivLGi1LCSM").build();
        jda.addEventListener(new event());
    }
}
