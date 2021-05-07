package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class avatar extends ListenerAdapter {
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().startsWith("!avatar"))
        {
            List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
            if (mentionedMembers.isEmpty()) {
                event.getChannel().sendMessage(CreateEmbed(event.getAuthor())).queue();
            }
            else
                event.getChannel().sendMessage(CreateEmbed((mentionedMembers.get(0).getUser()))).queue();
        }
    }

    public MessageEmbed CreateEmbed(User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setImage(user.getAvatarUrl());
        return embed.build();
    }
}