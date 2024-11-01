package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.dtos.UserDto;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.UserStatus;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hibernate.annotations.QueryHints.READ_ONLY;
import static org.hibernate.jpa.HibernateHints.HINT_CACHEABLE;
import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    // (1)Danh sách tất cả các bạn trong công ty có trạng thái tài khoản đang hoạt động và sắp xếp theo username
    List<User> findAllByStatusOrderByUsername(UserStatus status);


    // (2)Danh sách 10 bạn trong công ty có trạng thái tài khoản đang hoạt động và sắp xếp theo username
    List<User> findAllByStatus(UserStatus status, Pageable pageable);

    // (3.1)Danh sách 10 bạn nam trong công ty có trạng thái tài khoản đang hoạt động và được tạo trước năm 2023 sắp xếp theo username
    List<User> findAllByGenderAndStatusAndCreatedAtLessThan(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);

    // (3.2)
    @Query("""
    SELECT u
    FROM User u
    WHERE u.gender = :gender
      AND u.status = :status
      AND u.createdAt < :createdAt
  """)
    List<User> findTopUser(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);

    // (3.3)
    @Query("""
    SELECT new com.khanh.timekeeping.dtos.UserDto(u.id, u.fullName, u.username)
    FROM User u
    WHERE u.gender = :gender
      AND u.status = :status
      AND u.createdAt < :createdAt
  """)
    List<UserDto> findTopUserDto(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);

    // (3.4)
    @Query("""
    SELECT u
    FROM User u
    WHERE u.gender = :gender
      AND u.status = :status
      AND u.createdAt < :createdAt
  """)
    Page<User> findTopUserWithPage(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable);

    @QueryHints(
            value = {
                    @QueryHint(name = HINT_FETCH_SIZE, value = "" + 10),
                    @QueryHint(name = HINT_CACHEABLE, value = "false"),
                    @QueryHint(name = READ_ONLY, value = "true")
            })
    @Query("SELECT u FROM User u")
    Stream<User> getAll();

    Optional<User> findFirstByUsername(String username);

}
