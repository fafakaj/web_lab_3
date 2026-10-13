package ru.itmo.students.dto;

import ru.itmo.students.entity.Student;

public final class StudentMapper {
    private StudentMapper() {
    }

    public static StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getIsuId(),
                student.getFullName(),
                student.getGroup(),
                student.getDormitory(),
                student.getRoom(),
                student.getCheckIn(),
                student.getCheckOut(),
                student.isForeign(),
                student.getNotes());
    }

    public static void copyToEntity(StudentRequest request, Student student) {
        student.setFullName(request.fullName());
        student.setGroup(request.group());
        student.setDormitory(request.dormitory());
        student.setRoom(request.room());
        student.setCheckIn(request.checkIn());
        student.setCheckOut(request.checkOut());
        student.setForeign(request.isForeign());
        student.setNotes(request.notes());
    }
}
