package com.homework.velsera.cgccli.velsera.cgccli.commands;

import org.springframework.shell.table.ArrayTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public interface CliCommand {

    default void resetLastTerminalLine(){
        System.out.print("\33[2K\r");
        System.out.flush();
    }

    default String getShellTable(String[][] dataModel, boolean addHeader, boolean transposed){

        ArrayTableModel tableModel = new ArrayTableModel(dataModel);
        TableBuilder tableBuilder = new TableBuilder(transposed ? tableModel.transpose() : tableModel);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        if (addHeader) {
            tableBuilder.addHeaderAndVerticalsBorders(BorderStyle.fancy_double);
        }
        return tableBuilder.build().render(160);
    }

    default <T> List<List<String>> collectDataWithCounting(Iterator<T> iterator, int total, Function<T, List<String>> collector){
        List<List<String>> modelList = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(0);
        iterator.forEachRemaining(e -> {
            resetLastTerminalLine();
            int cnt = counter.incrementAndGet() ;
            System.out.print("Loading... " + cnt * 100 / total + " %");
            modelList.add(collector.apply(e));
        });
        resetLastTerminalLine();

        return modelList;
    }
}
