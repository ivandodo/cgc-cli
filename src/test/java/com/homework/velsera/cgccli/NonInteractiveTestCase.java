package com.homework.velsera.cgccli;

import static org.awaitility.Awaitility.await;

import com.homework.velsera.cgccli.velsera.cgccli.commands.FilesCommands;
import com.homework.velsera.cgccli.velsera.cgccli.commands.ProjectsCommands;
import com.homework.velsera.cgccli.velsera.cgccli.configuration.MethodMapping;
import com.homework.velsera.cgccli.velsera.cgccli.services.SbClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.test.ShellAssertions;
import org.springframework.shell.test.ShellTestClient;
import org.springframework.shell.test.autoconfigure.ShellTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@ShellTest(terminalWidth = 160, terminalHeight = 120)
@ContextConfiguration(classes = {MethodMapping.class, ProjectsCommands.class, FilesCommands.class, SbClientService.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class NonInteractiveTestCase {

    @Autowired
    ShellTestClient client;

    @Test
    void testHelp() {
        ShellTestClient.NonInteractiveShellSession session = client
                .nonInterative("help")
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("AVAILABLE COMMANDS");
        });
    }

    @Test
    void testMethodsRegistered() {
        ShellTestClient.NonInteractiveShellSession session = client
                .nonInterative("help")
                .run();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("projects list");
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("files list");
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("files stat");
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("files update");
        });

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            ShellAssertions.assertThat(session.screen())
                    .containsText("files download");
        });
    }
}