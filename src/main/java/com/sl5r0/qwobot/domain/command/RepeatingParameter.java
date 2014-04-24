package com.sl5r0.qwobot.domain.command;

import java.util.List;

public interface RepeatingParameter {
    List<String> parseArgumentsFrom(String string);
}

// constraints
// repeating parameters
//  - must be allowed 1) anywhere in the string, or 2) at the end of the string
// optional parameters
//  - must be at the end of the string only