package org.asupg.parser;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.asupg.parser.service.ExcelParserService;

import javax.inject.Inject;
import java.io.InputStream;

/**
 * Azure Functions with Azure Blob trigger.
 */
public class BlobTriggerJava {

    private final ExcelParserService excelParserService;

    @Inject
    public BlobTriggerJava(ExcelParserService excelParserService) {
        this.excelParserService = excelParserService;
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
        ) InputStream content,
        @BindingName("name") String name,
        final ExecutionContext context
    ) {
        context.getLogger().info("Parsing file: " + name);
        excelParserService.parse(content);
    }
}
