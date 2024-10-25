package pl.edu.agh.iisg.to.service;

import org.hibernate.Session;
import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.repository.StudentRepository;
import pl.edu.agh.iisg.to.session.TransactionService;

import java.util.*;

public class SchoolService {

    private final TransactionService transactionService;

    private final StudentDao studentDao;

    private final CourseDao courseDao;

    private final GradeDao gradeDao;

    private final StudentRepository studentRepository;

    public SchoolService(TransactionService transactionService, StudentDao studentDao, CourseDao courseDao, GradeDao gradeDao) {
        this.transactionService = transactionService;
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.gradeDao = gradeDao;
        this.studentRepository = new StudentRepository(studentDao, gradeDao, courseDao, transactionService);
    }

    public boolean enrollStudent(final Course course, final Student student) {
        if (student.courseSet().contains(course)) {
            return false;
        }

        try{
            return transactionService.doAsTransaction(() -> {
                student.courseSet().add(course);
                course.studentSet().add(student);
                studentDao.save(student);
                courseDao.save(course);

                return true;
            }).orElse(false);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeStudent(int indexNumber) {
        return transactionService.doAsTransaction(() -> {
            Optional<Student> optionalStudent = studentDao.findByIndexNumber(indexNumber);
            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();

//                for (Course course : student.courseSet()) {
//                    course.studentSet().remove(student);
//                    courseDao.save(course);
//                }
//
//                for (Grade grade : student.gradeSet()) {
//                    gradeDao.remove(grade);
//                }
//
//                studentDao.remove(student);
//                return true;
                this.studentRepository.remove(student);
            }
            return false;
        }).orElse(false);
    }

    public boolean gradeStudent(final Student student, final Course course, final float gradeValue) {
        try{
            return transactionService.doAsTransaction(() -> {
                Grade grade = new Grade(student, course, gradeValue);
                student.gradeSet().add(grade);
                course.gradeSet().add(grade);
                gradeDao.save(grade);
//                studentDao.save(student);
//                courseDao.save(course);
                return true;
            }).orElse(false);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public Map<String, List<Float>> getStudentGrades(String courseName) {
        Optional<Course> course = courseDao.findByName(courseName);
        if (course.isPresent()) {
            Course c = course.get();
            Map<String, List<Float>> grades = new HashMap<>();
            for(Grade grade : c.gradeSet()) {
                String student = grade.student().firstName() + " " + grade.student().lastName();

                if (!grades.containsKey(student)) {
                    grades.put(student, new ArrayList<>());
                }
                grades.get(student).add(grade.grade());
                grades.get(student).sort(Comparator.naturalOrder());
            }
            return grades;

        }
        return Collections.emptyMap();
    }
}
