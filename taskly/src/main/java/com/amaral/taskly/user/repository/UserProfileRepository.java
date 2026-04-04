package com.amaral.taskly.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaral.taskly.user.model.UserProfile;

import jakarta.transaction.Transactional;

@Transactional
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{

}
