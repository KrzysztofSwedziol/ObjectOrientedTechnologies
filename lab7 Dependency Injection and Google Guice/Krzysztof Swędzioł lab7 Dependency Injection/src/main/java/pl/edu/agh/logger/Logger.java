package pl.edu.agh.logger;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import pl.edu.agh.school.guice.SchoolModule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class Logger {
    protected static Logger logger;

    protected DateFormat dateFormat;

    protected Set<IMessageSerializer> registeredSerializers;

    public Logger(FileMessageSerializer fileMessageSerializer) {
        init();
        this.registeredSerializers = new HashSet<IMessageSerializer>();
        this.registeredSerializers.add(fileMessageSerializer);
    }

    public Logger(Set<IMessageSerializer> registeredSerializers) {
        init();
        if (registeredSerializers == null) {
            throw new IllegalArgumentException("null argument");
        }
        this.registeredSerializers = registeredSerializers;
    }

//    public static Logger getInstance() {
//        if (logger == null)
//            logger = new Logger();
//        return logger;
//    }

    public void registerSerializer(IMessageSerializer messageSerializer) {
        registeredSerializers.add(messageSerializer);
    }

    public void log(String message) {
        log(message, null);
    }

    public void log(String message, Throwable error) {
        for (IMessageSerializer messageSerializer : registeredSerializers) {
            String formattedMessage = dateFormat.format(new Date())
                    + ": " + message + (error != null ? error.toString() : "");
            messageSerializer.serializeMessage(formattedMessage);
        }
    }

    private void init() {
        dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }

}
