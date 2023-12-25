package etu2074.framework.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUpload {
    private String nom;
    private byte[] bytes;
    private String path;

    public FileUpload(String nom, byte[] bytes, String path) {
        setNom(nom);
        setBytes(bytes);
        setPath(path);
    }

    public FileUpload() {
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String name) {
        this.nom = name;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public void upload(String customPath) {
        upload(nom,bytes,customPath);
    }

    public void upload(){
        upload(nom,bytes,path);
    }


    private void upload(String nom,byte[]bytes,String path) {
        if (nom != null && bytes != null && path != null) {
            try {
                Path filePath = Paths.get(path+"/"+nom);
                Files.write(filePath, bytes);
                System.out.println("File uploaded successfully to path: " + path);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to upload the file.");
            }
        } else {
            System.err.println("Missing file data or path.");
        }
    }

    public static void download(String imagePath, String downloadPath) {
        try {
            Path sourcePath = Paths.get(imagePath);
            byte[] fileBytes = Files.readAllBytes(sourcePath);

            Path destinationPath = Paths.get(downloadPath);
            Files.write(destinationPath, fileBytes);
            System.out.println("File downloaded successfully to path: " + downloadPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to download the file.");
        }
    }
}
