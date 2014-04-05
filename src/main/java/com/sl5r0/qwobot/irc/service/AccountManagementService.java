package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.UserListEvent;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public class AccountManagementService extends AbstractIrcEventService {
    private final QwobotUserRepository userRepository;

    @Inject
    public AccountManagementService(EventBus eventBus, QwobotUserRepository userRepository) {
        super(eventBus);
        this.userRepository = checkNotNull(userRepository, "userRepository must not be null");
    }

    @Subscribe
    public void scanUserList(UserListEvent<PircBotX> event) {
        for (User user : event.getUsers()) {
            if (user.equals(event.getBot().getUserBot())) {
                continue;
            }
            greetUser(user);
        }
    }

    @Subscribe
    public void checkUserOnJoin(JoinEvent<PircBotX> event) {
        if (event.getUser().equals(event.getBot().getUserBot())) {
            return;
        }
        greetUser(event.getUser());
        event.getUser().send().message("Welcome back, " + event.getUser().getNick() + "!");
    }

    private void greetUser(User user) {
        final Optional<QwobotUser> qwobotUser = userRepository.findByNick(user.getNick());
        if (qwobotUser.isPresent()) {
            log.trace("Found existing user: " + qwobotUser);
        } else {
            user.send().message("Looks like this is the first time I've seen you, " + user.getNick() + ". Don't worry though, I've already created your account.");
            userRepository.save(new QwobotUser(user.getNick()));
        }
    }


}
