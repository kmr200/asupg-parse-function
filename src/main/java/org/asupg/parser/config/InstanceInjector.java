package org.asupg.parser.config;

import com.microsoft.azure.functions.spi.inject.FunctionInstanceInjector;
import org.asupg.parser.component.FunctionComponent;
import org.asupg.parser.component.DaggerFunctionComponent;

public class InstanceInjector implements FunctionInstanceInjector {

    private static final FunctionComponent COMPONENT = DaggerFunctionComponent.create();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> functionClass) throws Exception {
        return (T) COMPONENT.blobTriggerJava();
    }

}
