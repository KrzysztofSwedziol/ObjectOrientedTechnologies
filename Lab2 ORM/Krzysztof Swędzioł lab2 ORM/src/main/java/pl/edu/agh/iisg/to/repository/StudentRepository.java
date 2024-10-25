package pl.edu.agh.iisg.to.repository;

import pl.edu.agh.iisg.to.dao.StudentDao;
import pl.edu.agh.iisg.to.dao.CourseDao;
import pl.edu.agh.iisg.to.dao.GradeDao;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Student;
import pl.edu.agh.iisg.to.service.SchoolService;
import pl.edu.agh.iisg.to.session.TransactionService;

import java.util.ArrayList;
import java.util.Set;


import java.util.List;
import java.util.Optional;

public class StudentRepository implements Repository<Student> {
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private final GradeDao gradeDao;
    private final TransactionService transactionService;

    public StudentRepository(StudentDao studentDao, GradeDao gradeDao, CourseDao courseDao, TransactionService transactionService) {
        this.studentDao = studentDao;
        this.gradeDao = gradeDao;
        this.courseDao = courseDao;
        this.transactionService = transactionService;
    }

    @Override
    public Optional<Student> add(Student student) {
        return studentDao.save(student);
    }

    @Override
    public Optional<Student> getById(int id) {
        return studentDao.findById(id);
    }

    @Override
    public List<Student> findAll(){
        return studentDao.findAll();
    }

    @Override
    public void remove(Student student){
        student.courseSet().forEach(course -> { course.studentSet().remove(student); });
        student.gradeSet().forEach(grade -> {gradeDao.remove(grade);});
        studentDao.remove(student);
    }

    public Set<Student> findAllByCourseName(String courseName) {            //fabrycznie było List nie wiem dlaczego, Course ma Set
        Optional<Course> course = courseDao.findByName(courseName);
        if(course.isPresent()) {
            Course c = course.get();
            Set<Student> studentSet = c.studentSet();
//            List<Student> studentList = new ArrayList<>();
//            for(Student s : studentSet) {
//                studentList.add(s);
//            }
            return studentSet;
        }
        return null;
    }
}
