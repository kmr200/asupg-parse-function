package org.asupg.parser;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.asupg.parser.service.ExcelParserService;
import org.asupg.parser.service.impl.ExcelParserServiceImpl;

import java.io.ByteArrayInputStream;

/**
 * Azure Functions with Azure Blob trigger.
 */
public class BlobTriggerJava {

    private final ExcelParserService excelParserService;

    public BlobTriggerJava() {
        this.excelParserService = new ExcelParserServiceImpl();
    }

    /**
     * This function will be invoked when a new or updated blob is detected at the specified path. The blob contents are provided as input to this function.
     */
    @FunctionName("BlobTriggerJava")
    public void run(
        @BlobTrigger(
                name = "content",
                path = "reports/{name}",
                dataType = "binary",
                connection = "AzureWebJobsStorage"
        ) byte[] content,
        @BindingName("name") String name,
        final ExecutionContext context
    ) {
        context.getLogger().info("Parsing file: " + name);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
            excelParserService.parse(inputStream);
            context.getLogger().info("Successfully parsed file: " + name);
        } catch (Exception e) {
            context.getLogger().severe("Error parsing file: " + e.getMessage());
            throw new RuntimeException(e);
        };
    }
}
