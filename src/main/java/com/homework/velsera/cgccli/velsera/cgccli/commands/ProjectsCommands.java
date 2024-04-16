package com.homework.velsera.cgccli.velsera.cgccli.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.homework.velsera.cgccli.velsera.cgccli.services.SbClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.stereotype.Component;

import com.homework.velsera.cgccli.velsera.cgccli.utils.TerminalUtils;
import com.sevenbridges.apiclient.project.ProjectList;

@Component
public class ProjectsCommands {

    SbClientService sbClientService;

    @Autowired
    public ProjectsCommands(SbClientService sbClientService){
        this.sbClientService = sbClientService;
    }
    
    public String listProjects() {
        List<List<String>> projectsList = new ArrayList<>();
        ProjectList projectList = sbClientService.getClient().getProjects();
        int total = projectList.getSize();
        AtomicInteger counter = new AtomicInteger(0);

        projectList.iterator().forEachRemaining(e -> {
            TerminalUtils.resetLastTerminalLine();
            int cnt = counter.incrementAndGet() ;
            System.out.print("Loading projects..." + cnt * 100 / total + " %");
            projectsList.add(Arrays.asList(
                    e.getId(),
                    e.getName(),
                    e.getHref()));
        });
        TerminalUtils.resetLastTerminalLine();

        List<String> header = Arrays.asList("ID", "Project name", "Href");
        projectsList.add(0, header);

        String[][] data = projectsList.stream()
            .map(l -> l.stream().toArray(String[]::new))
            .toArray(String[][]::new);
        ArrayTableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        tableBuilder.addHeaderAndVerticalsBorders(BorderStyle.fancy_double);

        return tableBuilder.build().render(80);
    }
}