package com.sl5r0.qwobot.core;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sl5r0.qwobot.domain.QwobotUser;
import com.sl5r0.qwobot.persistence.QwobotUserRepository;
import org.pircbotx.User;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

@Singleton
public class UserManager {
    private final QwobotUserRepository userRepository;
    private final Set<Long> verifiedUserIds = newHashSet();

    @Inject
    public UserManager(QwobotUserRepository userRepository) {
        this.userRepository = checkNotNull(userRepository, "userRepository must not be null");
    }

    public Optional<QwobotUser> getUser(User user) {
        return userRepository.findByNick(user.getNick());
    }

    public boolean hasVerifiedUser(long userId) {
        return verifiedUserIds.contains(userId);
    }

    public void verifyUser(long userId) {
        checkArgument(userId >= 0, "userId must be >= 0");
        verifiedUserIds.add(userId);
    }
}
