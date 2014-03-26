package com.sl5r0.qwobot.persistence;

import org.junit.Before;
import org.junit.Test;

import static com.sl5r0.qwobot.core.TestModule.testInjector;

public class QwobotUserRepositoryTest {
    private QwobotUserRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = testInjector().instanceOf(QwobotUserRepository.class);
    }

    @Test
    public void ensureWeCanQueryByAuthenticationToken() throws Exception {
//        QwobotUser user = new QwobotUser(randomUUID(), "hostmask");
//        repository.save(user);

//        QwobotUser loadedUser = repository.findByAuthenticationToken(user.getAuthenticationToken()).get();
//        assertThat(loadedUser, is(equalTo(user)));
    }

    @Test
    public void ensureWeCanQueryByHostMask() throws Exception {
//        QwobotUser user = new QwobotUser(randomUUID(), "hostmask");
//        repository.save(user);
//
//        QwobotUser loadedUser = repository.findByHostMask(user.getHostMask()).get();
//        assertThat(loadedUser, is(equalTo(user)));
    }

}
