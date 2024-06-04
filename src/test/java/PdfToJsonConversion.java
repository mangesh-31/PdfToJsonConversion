import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfToJsonConversion {

    @Test
    public static void convertPdfFileToJson() {

        String inputPdfPath = "src/test/resources/What is Software Testing.pdf";
        String outputJsonPath = "src/test/resources/What is Software Testing.json";

        List<String> contentList = new ArrayList<>();
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputPdfPath))) {
            int numPages = pdfDoc.getNumberOfPages();

            for (int i = 1; i <= numPages; i++) {
                PdfPage page = pdfDoc.getPage(i);
                String pageContent = PdfTextExtractor.getTextFromPage(page);
                contentList.add(pageContent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create JSON object
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ArrayNode pagesArray = mapper.createArrayNode();

        // Add page contents to JSON array
        for (int i = 0; i < contentList.size(); i++) {
            ObjectNode pageNode = mapper.createObjectNode();
            pageNode.put("Page", i + 1);
            // Split content by lines and add to JSON object with line number as key
            String[] lines = contentList.get(i).split("\\r?\\n");
            ObjectNode linesObject = mapper.createObjectNode();
            for (int j = 0; j < lines.length; j++) {
                linesObject.put(Integer.toString(j + 1), lines[j]);
            }
            pageNode.set("Content", linesObject);
            pagesArray.add(pageNode);
        }

        File outputJsonFile = new File(outputJsonPath);
        // Write JSON to file
        try {
            mapper.writeValue(outputJsonFile, pagesArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Content stored in " + outputJsonFile.getName());
    }
}
