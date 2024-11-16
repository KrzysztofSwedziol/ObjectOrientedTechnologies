package pl.edu.agh.school.guice;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import pl.edu.agh.logger.FileMessageSerializer;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.demo.SchoolDemo;
import pl.edu.agh.school.persistence.PersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {
    @Provides
    PersistenceManager providePersistenceManager(){
        Injector injector = Guice.createInjector(new SchoolModule());
        SerializablePersistenceManager manager = injector.getInstance(SerializablePersistenceManager.class);
        //SerializablePersistenceManager manager = new SerializablePersistenceManager();
//        String newTeacherFileName = "guice-teachers.dat";
//        String newClassFileName = "guice-classes.dat";
//        manager.setTeachersStorageFileName(newTeacherFileName);
//        manager.setClassStorageFileName(newClassFileName);
        return manager;
    }

    @Provides
    @Named("guice-teachers.dat")
    String provideTeachersStorageFileName() {
        return "guice-teachers.dat";
    }

    @Provides
    @Named("guice-classes.dat")
    String provideClassesStorageFileName() {
        return "guice-classes.dat";
    }

    @Provides
    @Singleton
    Logger getLogger(){
        Injector injector = Guice.createInjector(new SchoolModule());
        FileMessageSerializer fileMessageSerializer = new FileMessageSerializer("persistence.log");
        //Logger logger = injector.getInstance(Logger.class);
        Logger logger = new Logger(fileMessageSerializer);
        return logger;
    }

}
