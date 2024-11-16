package pl.edu.agh.school.persistence;

import pl.edu.agh.school.SchoolClass;
import pl.edu.agh.school.Teacher;

import java.util.List;

public interface PersistenceManager {
    public List<Teacher> loadTeachers();
    public List<SchoolClass> loadClasses();
    public void saveTeachers(List<Teacher> teachers);
    public void saveClasses(List<SchoolClass> classes);
}
