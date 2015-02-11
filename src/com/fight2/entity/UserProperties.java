package com.fight2.entity;

public class UserProperties {
    public static final int MAX_STAMINA = 100;
    private int stamina;
    private int ticket;
    private int coin;
    private int guildContrib;

    public int getStamina() {
        return stamina;
    }

    public void setStamina(final int stamina) {
        this.stamina = stamina;
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(final int ticket) {
        this.ticket = ticket;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(final int coin) {
        this.coin = coin;
    }

    public int getGuildContrib() {
        return guildContrib;
    }

    public void setGuildContrib(final int guildContrib) {
        this.guildContrib = guildContrib;
    }

}
