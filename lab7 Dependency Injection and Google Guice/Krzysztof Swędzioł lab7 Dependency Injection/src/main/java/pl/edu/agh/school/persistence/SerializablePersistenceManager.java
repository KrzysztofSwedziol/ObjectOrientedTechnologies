package pl.edu.agh.school.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.SchoolClass;
import pl.edu.agh.school.Teacher;

public final class SerializablePersistenceManager implements PersistenceManager{

    private Logger log;

    private String teachersStorageFileName;

    private String classStorageFileName;

    @Inject
    public SerializablePersistenceManager(Logger logger) {
        teachersStorageFileName = "teachers.dat";
        classStorageFileName = "classes.dat";
        log = logger;
    }

    public void saveTeachers(List<Teacher> teachers) {
        if (teachers == null) {
            log.log("Attempt to save null teachers list");
            throw new IllegalArgumentException();
        }
        log.log("Saving teachers data to file: " + teachersStorageFileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(teachersStorageFileName))) {
            oos.writeObject(teachers);
            log.log("Teachers data successfully saved to file: " + teachersStorageFileName);
        } catch (FileNotFoundException e) {
            log.log("Error: Teachers storage file not found: " + teachersStorageFileName, e);
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            log.log("There was an error while saving the teachers data", e);
            removeFile(teachersStorageFileName);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Teacher> loadTeachers() {
        ArrayList<Teacher> res = null;
        log.log("Loading teachers data from file: " + teachersStorageFileName);
        try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(teachersStorageFileName))) {
            res = (ArrayList<Teacher>) ios.readObject();
            log.log("Teachers data successfully loaded from file: " + teachersStorageFileName);
        } catch (FileNotFoundException e) {
            log.log("Teachers file not found, returning empty list.", e);
            res = new ArrayList<>();
        } catch (IOException e) {
            log.log("There was an error while loading the teachers data", e);
        } catch (ClassNotFoundException e) {
            log.log("Class not found exception while loading teachers", e);
            throw new IllegalArgumentException(e);
        }
        return res;
    }

    public void saveClasses(List<SchoolClass> classes) {
        if (classes == null) {
            log.log("Attempt to save null classes list");
            throw new IllegalArgumentException("Classes list cannot be null");
        }

        log.log("Saving classes data to file: " + classStorageFileName);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(classStorageFileName))) {
            oos.writeObject(classes);
            log.log("Classes data successfully saved to file: " + classStorageFileName);
        } catch (FileNotFoundException e) {
            log.log("Error: Classes storage file not found: " + classStorageFileName, e);
            throw new IllegalArgumentException("File not found: " + classStorageFileName, e);
        } catch (IOException e) {
            log.log("There was an error while saving the classes data", e);
            removeFile(classStorageFileName);
        }
    }

    @SuppressWarnings("unchecked")
    public List<SchoolClass> loadClasses() {
        ArrayList<SchoolClass> res = null;
        log.log("Loading classes data from file: " + classStorageFileName);
        try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(classStorageFileName))) {
            res = (ArrayList<SchoolClass>) ios.readObject();
            log.log("Classes data successfully loaded from file: " + classStorageFileName);
        } catch (FileNotFoundException e) {
            log.log("Classes file not found, returning empty list.", e);
            res = new ArrayList<>();
        } catch (IOException e) {
            log.log("There was an error while loading the classes data", e);
            removeFile(classStorageFileName);  // Usuwamy plik w przypadku błędu
        } catch (ClassNotFoundException e) {
            log.log("Class not found exception while loading classes", e);
            throw new IllegalArgumentException("Class not found during loading classes", e);
        }
        return res;
    }
    private void removeFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.delete()) {
            log.log("File " + fileName + " deleted successfully.");
        } else {
            log.log("Failed to delete file: " + fileName);
        }
    }

    @Inject
    public void setTeachersStorageFileName(@Named("guice-teachers.dat") String filePath) {
        this.teachersStorageFileName = filePath;
    }

    @Inject
    public void setClassStorageFileName(@Named("guice-classes.dat") String filePath) {
        this.classStorageFileName = filePath;
    }
}
