package com.khanh.timekeeping.services.query;


import com.khanh.timekeeping.dtos.UserDto;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;

    private void showResult(String description, List<?> objects) {
        if (!CollectionUtils.isEmpty(objects)) {
            log.info(description);
            objects.forEach(object -> log.info(object.toString()));
        }
    }

    public void showQuery() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofYearDay(2023, 1), LocalTime.of(0, 0, 0));

        List<User> lstUser1 = userRepository.findAllByStatusOrderByUsername(User.STATUS_ACTIVE);
        showResult(
                "Danh sách tất cả các bạn trong công ty (status: Active) và sắp xếp theo username: ",
                lstUser1);

        List<User> lstUser2 = userRepository.findAllByStatus(User.STATUS_ACTIVE, pageable);
        showResult(
                "Danh sách 10 bạn trong công ty (status: Active) và sắp xếp theo username: ", lstUser2);

        List<User> lstUser31 =
                userRepository.findAllByGenderAndStatusAndCreatedAtLessThan(
                        User.GENDER_MALE, User.STATUS_ACTIVE, dateTime, pageable);
        showResult(
                "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
                lstUser31);

        List<User> lstUser32 =
                userRepository.findTopUser(User.GENDER_MALE, User.STATUS_ACTIVE, dateTime, pageable);
        showResult(
                "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
                lstUser32);

        List<UserDto> lstUser33 =
                userRepository.findTopUserDto(User.GENDER_MALE, User.STATUS_ACTIVE, dateTime, pageable);
        showResult(
                "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
                lstUser33);

        Page<User> lstUser34 =
                userRepository.findTopUserWithPage(
                        User.GENDER_MALE, User.STATUS_ACTIVE, dateTime, pageable);
        log.info("Tổng số phần tử: {}", lstUser34.getTotalElements());
        log.info("Tổng số trang: {}", lstUser34.getTotalPages());

        List<User> lstUser41 = userRepository.searchTopUser(User.GENDER_MALE, null, dateTime, pageable);
        showResult("Tìm kiếm danh sách người dùng", lstUser41);

        List<User> lstUser42 =
                userRepository.searchTopUserWithNativeQuery(User.GENDER_MALE, null, dateTime, pageable);
        showResult("Tìm kiếm danh sách người dùng", lstUser42);
    }

    @Transactional(readOnly = true)
    public void fetchAllUser() {
        Stream<User> userStream = userRepository.getAll();
        userStream.forEach(
                user -> {
                    log.info(user.getUsername());
                    entityManager.detach(user);
                });
    }
}
