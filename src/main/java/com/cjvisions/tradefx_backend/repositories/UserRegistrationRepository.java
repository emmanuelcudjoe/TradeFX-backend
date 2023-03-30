package com.cjvisions.tradefx_backend.repositories;

import com.cjvisions.tradefx_backend.domain.models.UserRegistrationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistrationInfo, String> {

    UserRegistrationInfo findByEmail(String email);
}
