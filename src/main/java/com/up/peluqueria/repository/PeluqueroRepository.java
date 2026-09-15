package com.up.peluqueria.repository;

import com.up.peluqueria.entity.Peluquero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeluqueroRepository extends JpaRepository<Peluquero, Long> {

    Optional<Peluquero> findByName(String name);
}
