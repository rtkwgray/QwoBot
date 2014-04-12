package com.sl5r0.qwobot.domain.help;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CommandDirectoryTest {
    private CommandDirectory directory;

    @Before
    public void setUp() throws Exception {
        directory = new CommandDirectory();
        directory.register(Sets.newHashSet(new Command("!z:c", "z:c"), new Command("!a:b", "a:b"), new Command("!a", "a")));
    }

    @Test
    public void ensureSearchLimitWorksProperly() throws Exception {
        assertThat(directory.search("", 2), hasSize(2));
    }

    @Test
    public void ensureSearchReturnsCorrectSortedResults() throws Exception {
        final List<Command> results = directory.search("!a", 2);
        assertThat(results, hasSize(2));
        assertThat(results.get(0).trigger(), is("!a"));
        assertThat(results.get(1).trigger(), is("!a:b"));
    }
}
