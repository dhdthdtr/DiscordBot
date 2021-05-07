package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class event extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
//        String name = event.getMember().getUser().getName();

        if(messageSent.equalsIgnoreCase("hello")) {
            if(!event.getMember().getUser().isBot()) {
                event.getChannel().sendMessage("Hello! " + event.getMember().getAsMention()).queue();
            }
        }
        if(messageSent.contains("alo alo")) {
            event.getChannel().sendMessage("lô lô cc").queue();
        }
    }
}
