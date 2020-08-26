package com.face.recognition.service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class TextService {

    public String detectText(MultipartFile file) {
        File conv = convertToFile(file);
        if (isNull(conv)) {
            return null;
        }
        String result = getText(conv);
        if (isNull(result)) {
            return null;
        }
        boolean deleteSuccess = conv.delete();
        if (!deleteSuccess) {
            log.info("Temp file was not deleted");
        }
        return result;
    }

    public String getText(File file) {
        String result = null;
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setTessVariable("user_defined_dpi", "300");
        try {
            result = tesseract.doOCR(file);
        } catch (Exception e) {
            log.info("Could not read from file: ", e);
        }
        return result;
    }

    public static File convertToFile(MultipartFile file) {
        try {
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            return convFile;
        } catch (Exception e) {
            log.info("Could not convert file: ", e);
            return null;
        }
    }
}
