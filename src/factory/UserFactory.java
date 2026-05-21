package factory;

import models.*;
import enumerations.Faculty;
import enumerations.GraduateLevel;
import enumerations.ManagerTypes;
import enumerations.TeacherType;
import exceptions.LowHIndexException;

public class UserFactory {

    private UserFactory() {}

    public static User createUser(String role,
                                  String userId, String name,
                                  String password, String login,
                                  Object... extraArgs) {
        return switch (role.toUpperCase()) {

            case "STUDENT" -> {
                Faculty faculty      = (Faculty) extraArgs[0];
                int     yearsOfStudy = (int)     extraArgs[1];
                yield new Student(userId, name, password, login, yearsOfStudy, faculty);
            }

            case "TEACHER" -> {
                Faculty faculty = (Faculty) extraArgs[0];
                TeacherType type = (TeacherType) extraArgs[1];
                double salary = (double) extraArgs[2];
                Teacher teacher = new Teacher(userId, name, password, login, salary, faculty, type);
                if (type == TeacherType.PROFESSOR) teacher.setResearcher(true);
                yield teacher;
            }

            case "ADMIN" -> {
                double salary = (double) extraArgs[0];
                yield new Admin(userId, name, password, login, salary);
            }

            case "MANAGER" -> {
                double salary = (double) extraArgs[0];
                ManagerTypes mType = (ManagerTypes) extraArgs[1];
                yield new Manager(userId, name, password, login, salary, mType);
            }
            
            case "TECH_SUPPORT" -> {
                double salary = (double) extraArgs[0];
                yield new TechSupportSpecialist(userId, name, password, login, salary);
            }

            case "GRADUATED_STUDENT" -> {
                Faculty faculty = (Faculty) extraArgs[0];
                int yearsOfStudy = (int) extraArgs[1];
                GraduateLevel level = (GraduateLevel) extraArgs[2];
                ResearcherDecorator supervisor = (ResearcherDecorator) extraArgs[3];
                try {
                    GraduatedStudent gs = new GraduatedStudent(userId, name, password, login,
                            yearsOfStudy, faculty, supervisor, level);
                    gs.setResearcher(true);   // выпускник всегда исследователь
                    yield gs;
                } catch (LowHIndexException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }

            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
