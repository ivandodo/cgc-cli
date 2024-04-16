package com.homework.velsera.cgccli.velsera.cgccli.configuration;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CgcPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("CgcCli:>", 
            AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)
        );
    }
}