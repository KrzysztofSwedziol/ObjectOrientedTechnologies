package pl.edu.agh.iisg.to.dao;

import org.hibernate.Session;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.session.SessionService;

import javax.persistence.PersistenceException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StudentDao extends GenericDao<Student> {

    public StudentDao(SessionService sessionService) {
        super(sessionService, Student.class);
    }

    public Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        Student student = new Student(firstName, lastName, indexNumber);
        return this.save(student);
    }

    public List<Student> findAll() {
        Session session = currentSession();
        try{
            List<Student> students = session.createQuery("Select s from Student s order by s.lastName, s.firstName", Student.class)
                    .getResultList();
            return students;
        }catch(PersistenceException e){
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Optional<Student> findByIndexNumber(final int indexNumber) {
        Session session = currentSession();
        try{
            Student student = session.createQuery("Select s from Student s where s.indexNumber = :indexNum", Student.class)
                    .setParameter("indexNum", indexNumber)
                    .getSingleResult();
            return Optional.of(student);
        }catch (Exception e) {
            return Optional.empty();
        }
    }
}
