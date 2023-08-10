package org.example.services;

import org.example.entity.Discord.Guild;
import org.example.impl.GuildImpl;

public class GuildServices {
    private GuildImpl guildImpl;

    public GuildServices() {
        guildImpl = new GuildImpl();
    }

    public Guild getJoinedGuildInfo(String guildName, String guildID){
        return guildImpl.getJoinedGuildInfo(guildName, guildID);
    }

    public boolean checkExist(String id){
        return guildImpl.checkExist(id);
    }

    public boolean updateMail(String id, String mail){
        return guildImpl.updateMail(id, mail);
    }

    public boolean insertMail(String id, String name, String mail){
        return guildImpl.insertMail(id, name, mail);
    }

    public boolean checkExistMail(String mail){
        return guildImpl.checkExistMail(mail);
    }
}
