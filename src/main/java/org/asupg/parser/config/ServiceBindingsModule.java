package org.asupg.parser.config;

import dagger.Binds;
import dagger.Module;
import org.asupg.parser.service.ExcelParserService;
import org.asupg.parser.service.impl.ExcelParserServiceImpl;

import javax.inject.Singleton;

@Module
public abstract class ServiceBindingsModule {

    @Binds
    @Singleton
    public abstract ExcelParserService excelParserService(ExcelParserServiceImpl excelParserServiceImpl);

}
