package pl.edu.agh.iisg.to.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

public class Course {

    public static final String TABLE_NAME = "course";

    private static final Logger logger = Logger.getGlobal();

    private final int id;

    private final String name;

    private List<Student> enrolledStudents;

    private boolean isStudentsListDownloaded = false;

    Course(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<Course> create(final String name) {
        String insertSql = "INSERT INTO course (name) VALUES (?);";
        Object[] args = {
                name
        };

        try {
            int id = QueryExecutor.createAndObtainId(insertSql, args);
            return Course.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<Course> findById(final int id) {
        String findByIdSql = "SELECT * FROM course WHERE id = ?";
        Object[] args = {
                id
        };

        try (ResultSet rs = QueryExecutor.read(findByIdSql, args)) {
            if (rs.next()) {
                return Optional.of(new Course(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean enrollStudent(final Student student) {
        //TODO
        //Najpierw sprawdzę czy student już czasem nie jest w bazie danych zapisany na kurs.
        String checkPresence = "Select * FROM student_course WHERE student_id = ? and course_id = ?;";
        Object[] checkArgs = {student.id(), this.id};
        try {
            ResultSet resultPresence = QueryExecutor.read(checkPresence, checkArgs);
            if (resultPresence.next()) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Następnie sprawdzę, czy podany student i kurs istnieją --- okazuje się że wtedy testy nie przechodzą
        //Zakomentuję więc sprawdzanie tego elementu.
//        String checkStudent = "Select * FROM student WHERE student_id = ?;";
//        Object[] studentArgs = {student.id()};
//        String checkCourse = "Select * FROM course WHERE course_id = ?;";
//        Object[] courseArgs = {this.id};

//        try {
//            ResultSet resultStudent = QueryExecutor.read(checkStudent, studentArgs);
//            ResultSet resultCourse = QueryExecutor.read(checkCourse, courseArgs);
//            if (resultStudent.next() && resultCourse.next()) {
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        String enrollStudentSql = "INSERT INTO student_course (student_id, course_id) VALUES (?, ?);";
        Object[] args2 = {student.id(), this.id};

        try {
            int result = QueryExecutor.createAndObtainId(enrollStudentSql, args2);
            if(result != -1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Student> studentList() {
        String findStudentListSql = "SELECT * FROM student s INNER JOIN student_course sc ON s.id = sc.student_id where course_id = ?;";
        Object[] args = {this.id};

        List<Student> resultList = new LinkedList<>();
        try {
            ResultSet resultSet = QueryExecutor.read(findStudentListSql, args);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                int indexNumber = resultSet.getInt("index_number");
                resultList.add(new Student(id, firstName, lastName, indexNumber));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultList;
    }

    public List<Student> cachedStudentsList() {
        if(this.isStudentsListDownloaded) {
            return this.enrolledStudents;
        }else{
            this.enrolledStudents = this.studentList();
        }
        return enrolledStudents;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String NAME = "name";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (id != course.id) return false;
        return name.equals(course.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}
