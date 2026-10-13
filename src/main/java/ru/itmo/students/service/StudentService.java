package ru.itmo.students.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import ru.itmo.students.dto.PageResponse;
import ru.itmo.students.dto.StudentFilter;
import ru.itmo.students.dto.StudentMapper;
import ru.itmo.students.dto.StudentRequest;
import ru.itmo.students.dto.StudentResponse;
import ru.itmo.students.entity.Student;
import ru.itmo.students.exception.ConflictException;
import ru.itmo.students.exception.DataValidationException;
import ru.itmo.students.exception.ResourceNotFoundException;
import ru.itmo.students.repository.StudentRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class StudentService {
    private static final Logger LOG = Logger.getLogger(StudentService.class.getName());

    private static final LocalDate EARLIEST_CHECK_IN = LocalDate.of(2000, 1, 1);
    private static final int MAX_STAY_YEARS = 9;
    private static final DateTimeFormatter RU_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Inject
    StudentRepository repository;

    public PageResponse<StudentResponse> find(StudentFilter filter) {
        List<StudentResponse> items = repository.find(filter).stream()
                .map(StudentMapper::toResponse)
                .toList();
        return new PageResponse<>(items, filter.page(), filter.size(), repository.count(filter));
    }

    public StudentResponse get(String isuId) {
        return StudentMapper.toResponse(findOrThrow(isuId));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (request.isuId() == null) {
            throw new DataValidationException("isu_id", "Обязательное поле");
        }
        checkDates(request.checkIn(), request.checkOut());

        if (repository.existsById(request.isuId())) {
            throw new ConflictException("isu_id", "Студент с ИСУ " + request.isuId() + " уже есть");
        }

        Student student = new Student();
        student.setIsuId(request.isuId());
        StudentMapper.copyToEntity(request, student);
        repository.save(student);

        LOG.info("Создан студент " + student.getIsuId());
        return StudentMapper.toResponse(student);
    }

    @Transactional
    public StudentResponse replace(String isuId, StudentRequest request) {
        if (request.isuId() != null && !request.isuId().equals(isuId)) {
            throw new DataValidationException("isu_id", "ИСУ менять нельзя");
        }

        Student student = findOrThrow(isuId);
        checkDates(request.checkIn(), request.checkOut());
        StudentMapper.copyToEntity(request, student);

        LOG.info("Изменён студент " + isuId);
        return StudentMapper.toResponse(student);
    }

    @Transactional
    public void delete(String isuId) {
        repository.delete(findOrThrow(isuId));
        LOG.info("Удалён студент " + isuId);
    }

    private Student findOrThrow(String isuId) {
        return repository.findById(isuId)
                .orElseThrow(() -> new ResourceNotFoundException("Студент с ИСУ " + isuId + " не найден"));
    }

    private static void checkDates(LocalDate checkIn, LocalDate checkOut) {
        LocalDate latestCheckIn = LocalDate.now().plusYears(1);

        if (checkIn.isBefore(EARLIEST_CHECK_IN) || checkIn.isAfter(latestCheckIn)) {
            throw new DataValidationException("check_in",
                    "Дата заселения — от 01.01.2000 до " + latestCheckIn.format(RU_DATE));
        }

        if (checkOut == null) {
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new DataValidationException("check_out", "Дата выселения должна быть позже даты заселения");
        }

        LocalDate latestCheckOut = checkIn.plusYears(MAX_STAY_YEARS);
        if (checkOut.isAfter(latestCheckOut)) {
            throw new DataValidationException("check_out", "Дата выселения — не позже " + latestCheckOut.format(RU_DATE)
                    + ": проживание не дольше " + MAX_STAY_YEARS + " лет");
        }
    }
}
