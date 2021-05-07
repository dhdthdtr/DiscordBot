import command.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotLz {
    public static void main(String[] args) throws Exception {
        JDA jda = JDABuilder.createDefault("ODM4NjgxMTk3NTUwODk1MTY0.YI-ouw.hhXxIagdz1YJ--uG6Vohy3iGjPc").build();

        ListenerAdapter[] listenerAdapters = new ListenerAdapter[]{ new event(), new Command(), new avatar(), new VoiceChannelBot()};
        jda.addEventListener(listenerAdapters);
        jda.addEventListener(new Doge());
    }
}
