package com.sl5r0.qwobot.domain.help;

public class CommandTest {
//    @Test
//    public void ensureArgumentsAreParsedCorrectly() throws Exception {
//        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), string("a name"), number("a number")), "something");
//        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger something 90");
//        command.handle(event);
//
//        final List<String> parsedArguments = command.executions.get(event);
//        assertThat(parsedArguments, hasSize(3));
//        assertThat(parsedArguments, contains("!trigger", "something", "90"));
//    }
//
//    @Test
//    public void ensureCommandIsNotExecutedWhenMessageDoesNotMatch() throws Exception {
//        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), string("a name"), number("a number")), "something");
//        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!nomatch");
//        command.handle(event);
//
//        assertThat(command.executions.values(), hasSize(0));
//    }
//
//    @Test
//    public void ensureOptionalCommandsWorkAtEndOfString() throws Exception {
//        final TestableCommand<MessageEvent> command = new TestableCommand<>(MessageEvent.class, newArrayList(exactMatch("!trigger"), optional(number("a number"))), "something");
//        final MessageEvent<PircBotX> event = new PircBotTestableObjectFactory().messageEvent("!trigger");
//        command.handle(event);
//
//        assertThat(command.executions.values(), hasSize(1));
//    }
//
//    private static class TestableCommand<T extends GenericMessageEvent> extends Command<T> {
//        private Map<T, List<String>> executions = newHashMap();
//
//        private TestableCommand(Class<T> eventType, List<Parameter> parameters, String description) {
//            super(eventType, parameters, of(description), new CommandHandler<T>() {
//                @Override
//                public void handle(T event, List<String> arguments) {
//                    System.out.println("something");
//                }
//            });
//        }
//    }
//
//    @Test
//    public void test() throws Exception {
////        CommandLineParser parser = new GnuParser();
////        final CommandLine parse = parser.parse(new Options().addOption("b", true, "b's description"), new String[]{"something", "-b", "value", "argument1", "argument2"});
////        System.out.println(Arrays.toString(parse.getArgs()));
////        System.out.println(Arrays.toString(parse.getOptions()));
//
//        Scanner scanner = new Scanner("here's \"a fun\" string 10 haha");
//        System.out.println(scanner.next(Pattern.compile("\\S+")));
//        System.out.println(scanner.next("what"));
//        System.out.println(scanner.next("here's"));
//        System.out.println(scanner.next());
//        System.out.println(scanner.next());
//        System.out.println(scanner.next());
//        System.out.println(scanner.next());
//        System.out.println(scanner.next());
//        System.out.println(scanner.next());
//
////        Command.forEvent()
//    }

    // TODO:
    // should commands be associated with a particular event class?
    // perhaps a command can be generic and accept a class as a parameter with an abstract "execute" method
    // the execute method should call another method from the service class.
    // the abstract service should have a single subscribe entry point that listens to all events.
    // the service should look at the commands, find ones that take the specific event type, and then execute those events


}
