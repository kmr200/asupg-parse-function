package org.asupg.parser.component;

import dagger.Component;
import org.asupg.parser.BlobTriggerJava;
import org.asupg.parser.config.DaggerModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = DaggerModule.class)
public interface FunctionComponent {

    BlobTriggerJava getBlobTrigger();

}
