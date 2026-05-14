package com.goit.ulr_shortener.repository;

import com.goit.ulr_shortener.entity.Url;
import com.goit.ulr_shortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);

    List<Url> findAllByUser(User user);
}