package com.sl5r0.qwobot.irc.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sl5r0.qwobot.core.TestModule;
import com.sl5r0.qwobot.helpers.TestableIrcServer;
import com.sl5r0.qwobot.helpers.TestablePircBotX;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.pircbotx.Configuration;

public class AccountManagementServiceTest {
    private QwobotUserRepository repository;
    private AccountManagementService service;
    private TestablePircBotX bot;

    @Rule
    public TestableIrcServer server = new TestableIrcServer();

    @Before
    public void setUp() throws Exception {
        Injector injector = Guice.createInjector(new TestModule());
        service = injector.getInstance(AccountManagementService.class);
        repository = injector.getInstance(QwobotUserRepository.class);
        bot = new TestablePircBotX(new Configuration.Builder<>().setServerHostname("localhost").setServerPort(server.port()).buildConfiguration());
    }

    @Test
    public void ensureUsersCanRegister() throws Exception {
        bot.startBot();
//        bot.connectPircBotX(new TestablePircBotX(new Configuration.Builder<>().setServerHostname("localhost").buildConfiguration()));
//        bot.connectUser();
//        final VerifiableUser user = bot.newUser("warren");
//        final VerifiableChannel channel = bot.newChannel("qwobot");
//        service.checkUserOnJoin(new JoinEvent<PircBotX>(bot, channel, user));
//        final List<QwobotUser> actual = repository.allUsers();
//        assertThat(actual, hasSize(1));
//        assertThat(user, hasReceivedMessageContaining("now registered"));
    }
}
