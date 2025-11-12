package com.freshfold.repository;

import com.freshfold.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByEmail(String email);
    Optional<Personnel> findByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
    List<Personnel> findAllByOrderByRatingDesc();

    @Query("SELECT p FROM Personnel p LEFT JOIN p.orders o " +
            "WHERE o.status = 'DONE' " +
            "GROUP BY p.id ORDER BY COUNT(o) DESC")
    List<Personnel> findTopPerformers();
}