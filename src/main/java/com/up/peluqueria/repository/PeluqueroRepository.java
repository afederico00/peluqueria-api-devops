package com.up.peluqueria.repository;

import com.up.peluqueria.entity.Peluquero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeluqueroRepository extends JpaRepository<Peluquero, Long> {

    Optional<Peluquero> findByName(String name);
}
