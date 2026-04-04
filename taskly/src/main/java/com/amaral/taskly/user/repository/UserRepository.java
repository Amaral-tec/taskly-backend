package com.amaral.taskly.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.amaral.taskly.user.model.User;

import jakarta.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);

	Optional<User> findByPublicId(UUID publicId);

    @Transactional
	@Modifying
	@Query(nativeQuery = true, value = "INSERT INTO users_accesses(user_id, access_id) VALUES (?1, ?2)")
	void insertUserAccess(Long userId, Long accessId);
}
