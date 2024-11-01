package com.khanh.timekeeping.services.query;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.dtos.UserDto;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.UserStatus;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private  final QueryRedisService queryRedisService;

    private void showResult(String description, List<?> objects) {
        if (!CollectionUtils.isEmpty(objects)) {
            log.info(description);
            objects.forEach(object -> log.info(object.toString()));
        }
    }

    public void showQuery() throws JsonProcessingException {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username"));
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.ofYearDay(2023, 1), LocalTime.of(0, 0, 0));

//        List<User> existedLstUser1 =queryRedisService.getUserList("lstUser1");
//        List<User> existedLstUser1 = null;
//        if(existedLstUser1 == null) {
//            List<User> lstUser1 = userRepository.findAllByStatusOrderByUsername(UserStatus.ACTIVE);
//            showResult(
//                    "Danh sách tất cả các bạn trong công ty (status: Active) và sắp xếp theo username: ",
//                    lstUser1);
//            queryRedisService.saveUserList("lstUser1", lstUser1, 1, TimeUnit.HOURS);
//        }else {
////            System.out.println(existedLstUser1);
//        }

        List<User> existedLstUser2 =queryRedisService.getUserList("lstUser2");
//        List<User> existedLstUser2 = null;
        if(existedLstUser2 == null) {
            List<User> lstUser2 = userRepository.findAllByStatus(UserStatus.ACTIVE, pageable);
            queryRedisService.saveUserList("lstUser2", lstUser2, 1, TimeUnit.HOURS);
            showResult(
                    "Danh sách 10 bạn trong công ty (status: Active) và sắp xếp theo username: ", lstUser2);
        }else {
            System.out.println(existedLstUser2);
        }

        List<User> existedLstUser31 =queryRedisService.getUserList("lstUser31");
//        List<User> existedLstUser31 = null;
        if(existedLstUser31 == null) {
            List<User> lstUser31 =
                    userRepository.findAllByGenderAndStatusAndCreatedAtLessThan(
                            Gender.MALE, UserStatus.ACTIVE, dateTime, pageable);
            queryRedisService.saveUserList("lstUser31", lstUser31, 1, TimeUnit.HOURS);
            showResult(
                    "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
                    lstUser31);
        }else {
            System.out.println(existedLstUser31);
        }

        List<User> existedLstUser32 =queryRedisService.getUserList("lstUser32");
//        List<User> existedLstUser32 = null;
        if(existedLstUser32 == null) {
            List<User> lstUser32 =
                    userRepository.findTopUser(Gender.MALE, UserStatus.ACTIVE, dateTime, pageable);
            queryRedisService.saveUserList("lstUser32", lstUser32, 1, TimeUnit.HOURS);
            showResult(
                    "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
                    lstUser32);
        }else {
//            System.out.println(existedLstUser32);
        }
//        List<UserDto> lstUser33 =
//                userRepository.findTopUserDto(Gender.MALE, UserStatus.ACTIVE, dateTime, pageable);
//        queryRedisService.saveUserList("lstUser33", lstUser33, 1, TimeUnit.HOURS);
//        showResult(
//                "Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username: ",
//                lstUser33);

//        Page<User> lstUser34 =
//                userRepository.findTopUserWithPage(
//                        Gender.MALE, UserStatus.ACTIVE, dateTime, pageable);
//        log.info("Tổng số phần tử: {}", lstUser34.getTotalElements());
//        log.info("Tổng số trang: {}", lstUser34.getTotalPages());

        List<User> existedLstUser41 =queryRedisService.getUserList("lstUser41");
//        List<User> existedLstUser41 = null;
        if(existedLstUser41 == null) {
            List<User> lstUser41 = userRepository.searchTopUser(Gender.MALE, null, dateTime, pageable);
            queryRedisService.saveUserList("lstUser41", lstUser41, 1, TimeUnit.HOURS);
            showResult("Tìm kiếm danh sách người dùng", lstUser41);
        }else {
//            System.out.println(existedLstUser41);
        }
//        List<User> lstUser42 =
//                userRepository.searchTopUserWithNativeQuery(Gender.MALE, null, dateTime, pageable);
//        showResult("Tìm kiếm danh sách người dùng", lstUser42);
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
