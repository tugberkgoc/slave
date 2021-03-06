package com.goct;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Bot {

    private Bot() throws LoginException {
        EventWaiter eventWaiter = new EventWaiter();
        WebUtils.setUserAgent("Mozilla/5.0 slave#1745 / tugberkgoc#4859");

        new JDABuilder()
                .setToken(Config.get("token"))
                .addEventListeners(new Listener(eventWaiter), eventWaiter)
                .setActivity(Activity.watching("DeepTurkishWeb"))
                .build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }

}
