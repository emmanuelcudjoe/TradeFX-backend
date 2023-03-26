package com.cjvisions.tradefx_backend.repositories;

import com.cjvisions.tradefx_backend.domain.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {

    Provider findByContact(String contact);

    Provider findByName(String name);
}
