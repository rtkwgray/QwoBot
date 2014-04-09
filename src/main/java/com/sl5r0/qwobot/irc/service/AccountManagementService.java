package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.core.UserManager;
import com.sl5r0.qwobot.domain.QwobotUser;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.UserListEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.sl5r0.qwobot.domain.Role.securedAccount;
import static java.util.Collections.emptyList;

@Singleton
public class AccountManagementService extends AbstractIrcEventService {
    private final QwobotUserRepository userRepository;
    private final UserManager userManager;

    @Inject
    public AccountManagementService(EventBus eventBus, QwobotUserRepository userRepository, UserManager userManager) {
        super(eventBus);
        this.userManager = checkNotNull(userManager, "userManager must not be null");
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

    @Subscribe
    public void accountSecurity(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:security", event.getMessage());
        final Optional<QwobotUser> user = userRepository.findByNick(event.getUser().getNick());
        if (arguments.size() == 1) {
            checkAccountSecurity(event, user);
        } else if (arguments.size() == 2) {
            changeAccountSecurity(event, arguments.get(1), user);
        }
    }

    @Subscribe
    public void login(PrivateMessageEvent<PircBotX> event) {
        final List<String> arguments = argumentsFor("!account:login", event.getMessage());
        if (arguments.isEmpty()) {
            return;
        }

        final Optional<QwobotUser> user = userRepository.findByNick(event.getUser().getNick());
        if (user.isPresent()) {
            userManager.verifyUser(user.get().getId());
            event.respond("Success. You will remain logged in until I lose sight of you.");
        } else {
            event.respond("I don't know you.");
        }
    }

    private void checkAccountSecurity(PrivateMessageEvent<PircBotX> event, Optional<QwobotUser> user) {
        if (user.isPresent()) {
            if (user.get().hasRole(securedAccount())) {
                event.respond("Account security is currently ON. \"!account:security off\" will disable it.");
            } else {
                event.respond("Account security is currently OFF. \"!account:security on\" will enable it.");
            }
        }
    }

    @RequiresPermissions("account:modify") // TODO: make this not public
    public void changeAccountSecurity(PrivateMessageEvent<PircBotX> event, String newState, Optional<QwobotUser> user) {
        if (user.isPresent()) {
            switch (newState.toUpperCase()) {
                case "OFF":
                    user.get().removeRole(securedAccount());
                    event.respond("Account security has been disabled.");
                    break;

                case "ON":
                    user.get().addRole(securedAccount());
                    event.respond("Account security has been enabled.");
                    break;

                default:
                    event.respond("Usage: !account:security <on|off>");
            }
        }
    }

    private void greetUser(User user) {
        final Optional<QwobotUser> qwobotUser = userRepository.findByNick(user.getNick());
        if (qwobotUser.isPresent()) {
            log.trace("Found existing user: " + qwobotUser);
        } else {
            user.send().message("Looks like this is the first time I've seen you, " + user.getNick() + ". Don't worry though, I've already created your account.");
            final QwobotUser newUser = new QwobotUser(user.getNick());
            userRepository.save(newUser);
        }
    }

    private List<String> argumentsFor(String trigger, String message) {
        final Pattern PARAMETER_PATTERN = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        final Matcher matcher = PARAMETER_PATTERN.matcher(message);
        final List<String> parameters = newArrayList();
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parameters.add(matcher.group(1));
            } else {
                parameters.add(matcher.group(2));
            }
        }

        if (!parameters.isEmpty() && parameters.get(0).equals(trigger)) {
            return parameters;
        } else {
            return emptyList();
        }
    }


}
