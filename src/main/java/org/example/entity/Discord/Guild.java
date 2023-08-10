package org.example.entity.Discord;

public class Guild {
    public String id;
    public String guildName;
    public String prefix;
    public String premium_tier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuildName() {
        return guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPremium_tier() {
        return premium_tier;
    }

    public void setPremium_tier(String premium_tier) {
        this.premium_tier = premium_tier;
    }
}
