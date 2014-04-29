package com.sl5r0.qwobot.irc.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sl5r0.qwobot.domain.Account;
import com.sl5r0.qwobot.domain.command.Command;
import com.sl5r0.qwobot.domain.command.CommandHandler;
import com.sl5r0.qwobot.persistence.AccountRepository;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.sl5r0.qwobot.domain.Role.owner;
import static com.sl5r0.qwobot.domain.command.Parameter.literal;
import static com.sl5r0.qwobot.guice.ConfigurationProvider.readConfigurationValue;
import static com.sl5r0.qwobot.security.AccountManager.getActingAccount;

public class AdminService extends AbstractIrcEventService {
    private final AccountRepository accountRepository;
    private final Optional<String> botOwner;

    @Inject
    public AdminService(AccountRepository accountRepository, HierarchicalConfiguration configuration) {
        botOwner = readConfigurationValue(configuration, "bot.owner");
        this.accountRepository = checkNotNull(accountRepository, "accountRepository must not be null");
    }

    @Override
    protected void initialize() {
        registerCommand(Command.forEvent(PrivateMessageEvent.class)
                .description("Claim ownership of the bot")
                .addParameters(literal("!admin:init"))
                .handler(new CommandHandler<PrivateMessageEvent>() {
                    @Override
                    public void handle(PrivateMessageEvent event, List<String> arguments) {
                        initializeOwner(event);
                    }
                })
                .build());
    }

    private void initializeOwner(PrivateMessageEvent event) {
        final Account account = getActingAccount();
        if (!account.getPassword().isPresent()) {
            event.respond("Bot owner accounts must have a password set");
            return;
        }

        if (!botOwner.isPresent() || !botOwner.get().equals(getActingAccount().getUsername())) {
            event.respond("You're not listed as an owner in qwobot.xml");
            return;
        }

        account.addRole(owner());
        accountRepository.saveOrUpdate(account);
        event.respond("Hello, Master.");
    }
}
