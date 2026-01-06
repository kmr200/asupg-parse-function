package org.asupg.parser.component;

import dagger.Component;
import org.asupg.parser.BlobTriggerJava;
import org.asupg.parser.config.DaggerModule;
import org.asupg.parser.config.ServiceBindingsModule;

import javax.inject.Singleton;

@Singleton
@Component(
        modules = {
                DaggerModule.class,
                ServiceBindingsModule.class
        }
)
public interface FunctionComponent {

    BlobTriggerJava blobTriggerJava();

}
