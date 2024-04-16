package com.homework.velsera.cgccli.velsera.cgccli.configuration;

import java.util.function.Supplier;

import com.homework.velsera.cgccli.velsera.cgccli.commands.FilesCommands;
import com.homework.velsera.cgccli.velsera.cgccli.commands.ProjectsCommands;
import com.homework.velsera.cgccli.velsera.cgccli.services.SbClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.Availability;
import org.springframework.shell.command.CommandRegistration;
import org.springframework.stereotype.Component;

@Component
public class MethodMapping {
    SbClientService sbClientService;
    ProjectsCommands projectsCommands;
    FilesCommands filesCommands;

    @Autowired
    public MethodMapping(SbClientService sbClientService,
                            ProjectsCommands projectsCommands,
                            FilesCommands filesCommands){
        this.sbClientService = sbClientService;
        this.projectsCommands = projectsCommands;
        this.filesCommands = filesCommands;
    }

    private Supplier<Availability> getAvailability() {
        return () -> sbClientService.isValidClient()
        ? Availability.available()
        : Availability.unavailable("token or API endpoint is not set");
    }

    @Bean
    CommandRegistration projectsListCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("projects list")
            .group("projects")
            .description("List available projects")
            .availability(getAvailability())
            .withTarget()
                .method(projectsCommands, "listProjects")
                .and()
            .build();
    }

    @Bean
    CommandRegistration filesListCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("files list")
            .group("files")
            .description("List files in project")
            .availability(getAvailability())
            .withTarget()
                .method(filesCommands, "listFiles")
                .and()
            .withOption()
                .longNames("project")
                .label("<projectId>")
                .description("Id of the project whose files are being listed")
                .required()
                .arity(1,1)
                .and()
            .build();
    }

    @Bean
    CommandRegistration filesStatCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("files stat")
            .group("files")
            .description("Get file details")
            .availability(getAvailability())
            .withTarget()
                .method(filesCommands, "fileStat")
                .and()
            .withOption()
                .longNames("file")
                .label("<fileId>")
                .description("Id of file that is being described")
                .required()
                .arity(1,1)
                .type(String.class)
                .and()
            .build();
    }

    @Bean
    CommandRegistration filesUpdateCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("files update")
            .group("files")
            .description("Update file details")
            .availability(getAvailability())
            .withOption()
                .longNames("file")
                .label("<fileId> <updatePayload>")
                .description("<fileID> is id of file to be updated.\n<updatePayload> is property to be updated, can be name(name=newName), metadata(metadata.property=value) or tag(tag=newTag)")
                .required()
                .arity(2, 2)
                .type(String[].class)
                .and()
            .withTarget()
                .method(filesCommands, "updateFile")
                .and()
            .build();
    }

    @Bean
    CommandRegistration filesDownloadCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("files download")
            .group("files")
            .description("Download file")
            .availability(getAvailability())
            .withOption()
                .longNames("file")
                .label("<fileId>")
                .description("id of file that is being downloaded")
                .required()
                .arity(1,1)
                .type(String.class)
                .and()
            .withOption()
                .longNames("dest")
                .label("<destinationFile>")
                .description("Destination includes file name. Relative paths supported.")
                .required()
                .arity(1,1)
                .type(String.class)
                .and()
            .withTarget()
                .method(filesCommands, "downloadFile")
                .and()
            .build();
    }

    /**
     * Used for development purposes only
     */
    @Bean
    CommandRegistration tokenCheckCommandRegistration(CommandRegistration.BuilderSupplier builder) {

        return builder.get()
            .command("connectivity check")
            .group("utils")
            .hidden(true)
            .description("Check connection validity")
            .withTarget()
                .method(sbClientService, "clientValidityCheck")
                .and()
            .build();
    }
}
