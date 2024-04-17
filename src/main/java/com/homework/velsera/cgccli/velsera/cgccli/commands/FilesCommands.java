package com.homework.velsera.cgccli.velsera.cgccli.commands;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.homework.velsera.cgccli.velsera.cgccli.services.SbClientService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sevenbridges.apiclient.file.File;
import com.sevenbridges.apiclient.file.FileList;

@Component
public class FilesCommands implements CliCommand {

    final SbClientService sbClientService;

    @Autowired
    public FilesCommands(SbClientService sbClientService){
        this.sbClientService = sbClientService;
    }

    public String listFiles(String project) {
        String[][] filesModel = collectFilesModel(project);
        return getShellTable(filesModel, true, false);
    }
    
    private String[][] collectFilesModel(String projectId){
        FileList cgcFileList = sbClientService.getClient().getProjectById(projectId).getFiles();
        Function<File, List<String>> rowCollector = f -> Arrays.asList(f.getId(),
                f.getName(),
                f.getCreatedOn().toString(),
                f.getModifiedOn().toString(),
                FileUtils.byteCountToDisplaySize(f.getSize()));
        List<List<String>> modelList = collectDataWithCounting(cgcFileList.iterator(), cgcFileList.getSize(), rowCollector);

        List<String> header = Arrays.asList("ID", "File name", "Created on", "Modified on", "File size");
        modelList.add(0, header);

        return modelList.stream()
                .map(l -> l.toArray(String[]::new))
                .toArray(String[][]::new);
    }

    public String fileStat(String file) {
        Map<String, String> stats = new LinkedHashMap<>();
        File cgcFile = sbClientService.getClient().getFileById(file);
        stats.put("ID", cgcFile.getId());
        stats.put("Name", cgcFile.getName());
        stats.put("Size", FileUtils.byteCountToDisplaySize(cgcFile.getSize()));
        stats.put("Href", cgcFile.getHref());
        stats.put("Project", cgcFile.getProject().getName());
        stats.put("Created on ", cgcFile.getCreatedOn().toString());
        stats.put("Modified on", cgcFile.getModifiedOn().toString());

        //collect storage data into subtable
        Map<String, String> storageMap = new LinkedHashMap<>();
        storageMap.put("Type", cgcFile.getFileStorage().getType().toString());
        storageMap.put("Volume", cgcFile.getFileStorage().getVolume());
        storageMap.put("Location", cgcFile.getFileStorage().getLocation());
        String[][] storage = storageMap.entrySet().stream().map(e -> new String[]{e.getKey(), e.getValue()}).toArray(String[][]::new);
        stats.put("Storage", getShellTable(storage, false, true));

        stats.put("Origin", cgcFile.getFileOrigin() != null ? "Dataset: " + cgcFile.getFileOrigin().getTask() : "");
        stats.put("Tags", String.join(", ", cgcFile.getTags()));

        //collect metadata into subtable
        Map<String, String> metadataMap = cgcFile.getMetadata();
        String[][] metadata = metadataMap.entrySet().stream().map(e -> new String[]{e.getKey(), e.getValue()}).toArray(String[][]::new);
        stats.put("Metadata", getShellTable(metadata, false, false));

        String[][] list = stats.entrySet().stream().map(e -> new String[]{e.getKey(), e.getValue()}).toArray(String[][]::new);
        return getShellTable(list, true, false);
    }

    public String updateFile(String[] file) {
        //TODO: move file fetch after validations
        File cgcFile = sbClientService.getClient().getFileById(file[0]);
        String[] updateData = file[1].split("=");
        if (updateData.length != 2){
            return "Malformed update data parameter";
        }
        String propToUpdate = updateData[0];
        String propValue = updateData[1];
        String[] propPath = propToUpdate.split("\\.");
        if (propPath[0].equals("name") && propPath.length == 1){
            cgcFile.setName(propValue).save();
        } else if (propPath[0].equals("metadata") && propPath.length == 2) {
            Map<String, String> metaPatch = new HashMap<>();
            metaPatch.put(propPath[1], propValue);
            cgcFile.patchMetadata(metaPatch).save();
        } else if (propPath[0].equals("tag") && propPath.length == 1) {
            Set<String> tags = cgcFile.getTags();
            tags.add(propValue);
            cgcFile.setTags(tags).save();
        } else {
            return "Property to update not recognized";
        }
        return "Updated file: " + cgcFile.getName() + ", id: " + cgcFile.getId() + ", data: " + file[1];
    }

    public String downloadFile(String file, String dest) {

        System.out.println("Download starting...");
        File srcFile = sbClientService.getClient().getFileById(file);
        String uri = srcFile.getDownloadInfo().getUrl();
        URL website;
        try {
            website = new URL(uri);
        } catch (MalformedURLException e) {
            return "ERROR: Cannot get file URL";
        }
        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream())) {
            java.io.File destFile = new java.io.File(dest);
            if (destFile.getAbsolutePath().isEmpty() 
                || destFile.getParentFile() == null){
                return "ERROR: Malformed destination path";
            }
            try (FileOutputStream fos = new FileOutputStream(destFile.getAbsolutePath())) {
                long size = srcFile.getSize();
                long transferred = 0;
                //TODO: maybe increase chunk size for bigger files and add estimation
                long chunkSize = 1024*1024; 
                do {
                    transferred += fos.getChannel().transferFrom(rbc, transferred, chunkSize);
                    resetLastTerminalLine();
                    System.out.print(transferred * 100 / size + " %");
                } while (transferred < size);
                resetLastTerminalLine();
            } catch (IOException e) {
                return "ERROR: Cannot access download location: " + destFile.getAbsolutePath();
            }
        } catch (IOException e) {
            return "ERROR: downloading fil interrupted";
        }
        return "Download complete!";
    }
}