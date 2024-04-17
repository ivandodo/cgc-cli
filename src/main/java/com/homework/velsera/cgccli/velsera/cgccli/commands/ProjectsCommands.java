package com.homework.velsera.cgccli.velsera.cgccli.commands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.homework.velsera.cgccli.velsera.cgccli.services.SbClientService;
import com.sevenbridges.apiclient.project.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.sevenbridges.apiclient.project.ProjectList;

@Component
public class ProjectsCommands implements CliCommand {

    final SbClientService sbClientService;

    @Autowired
    public ProjectsCommands(SbClientService sbClientService){
        this.sbClientService = sbClientService;
    }
    
    public String listProjects() {
        String[][] projectsModel = collectProjectsModel();
        return getShellTable(projectsModel, true, false);
    }

    private String[][] collectProjectsModel(){
        ProjectList projectList = sbClientService.getClient().getProjects();
        Function<Project, List<String>> rowCollector = p -> Arrays.asList(
                p.getId(),
                p.getName(),
                p.getHref());
        List<List<String>> modelList = collectDataWithCounting(projectList.iterator(), projectList.getSize(), rowCollector);

        List<String> header = Arrays.asList("ID", "Project name", "Href");
        modelList.add(0, header);

        return modelList.stream()
                .map(l -> l.toArray(String[]::new))
                .toArray(String[][]::new);
    }
}