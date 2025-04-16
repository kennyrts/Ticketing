package com.example.ticketing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ticketing.models.Enfant;

public interface EnfantRepository extends JpaRepository<Enfant, Integer> {
} 