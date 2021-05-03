package event;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class event extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] messageSent = event.getMessage().getContentRaw().split(" ");
        String name = event.getMember().getUser().getName();
        if(messageSent[0].equalsIgnoreCase("hello")) {
            if(!event.getMember().getUser().isBot()) {
                event.getChannel().sendMessage("Hello ! @" + name).queue();
            }
        }
        else if(messageSent[0].equalsIgnoreCase("Time")) {
            if(!event.getMember().getUser().isBot()){
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                event.getChannel().sendMessage("Time: " + formatter.format(date)).queue();
            }
        }
    }
}
