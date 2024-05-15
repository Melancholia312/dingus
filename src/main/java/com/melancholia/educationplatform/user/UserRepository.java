package com.melancholia.educationplatform.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    List<User> findByGroupId(long groupId);
    List<User> findByIsTeacherTrue();
    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = FALSE WHERE a.email = ?1")
    int banAppUser(String email);

    @Modifying
    @Query(value = "insert into users_privileges (user_id, privilege_id) VALUES (:userId, :permissionId)", nativeQuery = true)
    @Transactional
    void addPermission(@Param("userId") long userId, @Param("permissionId") long permissionId);

    @Modifying
    @Query(value = "delete from course_users WHERE user_id=:userId AND course_id=:courseId", nativeQuery = true)
    @Transactional
    void unsubscribe(@Param("userId") long userId, @Param("courseId") long courseId);

    @Modifying
    @Query(value = "delete from users_privileges WHERE user_id=:userId AND privilege_id=:permissionId", nativeQuery = true)
    @Transactional
    void deletePermission(@Param("userId") long userId, @Param("permissionId") long permissionId);
}
