package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Cat extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        String CatAPI = "https://api.thecatapi.com/v1/images/search";
        EmbedBuilder embed = new EmbedBuilder();

        if(messageSent.equalsIgnoreCase("!cat")) {
            embed.setTitle("Your cat: ");

            event.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
