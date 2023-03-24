package com.cjvisions.tradefx_backend.repositories;

import com.cjvisions.tradefx_backend.domain.models.UserLoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLoginInfo, String> {

    UserLoginInfo findByEmail(String email);
}
