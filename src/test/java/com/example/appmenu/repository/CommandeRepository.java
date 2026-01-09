package com.example.appmenu.repository;

import com.example.appmenu.entity.Commande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findTopByTableNumOrderByCreatedAtDesc(int tableNum);

    Page<Commande> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
        SELECT 
            COUNT(c) as total,
            SUM(CASE WHEN c.status = 'en_attente' THEN 1 ELSE 0 END) as enAttente,
            SUM(CASE WHEN c.status = 'preparation' THEN 1 ELSE 0 END) as preparation,
            SUM(CASE WHEN c.status = 'prete' THEN 1 ELSE 0 END) as prete,
            COALESCE(SUM(c.total), 0) as chiffreAffaires
        FROM Commande c
        WHERE DATE(c.createdAt) = :date
        """)
    StatsProjection getStatsByDate(@Param("date") LocalDate date);

    // Alternative si la projection ne fonctionne pas
    @Query("""
        SELECT 
            COUNT(c),
            SUM(CASE WHEN c.status = 'en_attente' THEN 1 ELSE 0 END),
            SUM(CASE WHEN c.status = 'preparation' THEN 1 ELSE 0 END),
            SUM(CASE WHEN c.status = 'prete' THEN 1 ELSE 0 END),
            COALESCE(SUM(c.total), 0)
        FROM Commande c
        WHERE DATE(c.createdAt) = :date
        """)
    Object[] getStatsByDateArray(@Param("date") LocalDate date);

    Page<Commande> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    @Query("SELECT c FROM Commande c WHERE c.createdAt BETWEEN :start AND :end ORDER BY c.createdAt DESC")
    Page<Commande> findByPeriod(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end,
                                Pageable pageable);

    @Query("SELECT COUNT(c) FROM Commande c WHERE c.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT c FROM Commande c ORDER BY c.createdAt DESC")
    List<Commande> findTopNRecent(Pageable pageable);

    @Query("SELECT c FROM Commande c WHERE c.status = 'en_attente' ORDER BY c.createdAt ASC")
    List<Commande> findCommandesEnAttente();

    @Query("""
        SELECT 
            DATE(c.createdAt) as date,
            COUNT(c) as total,
            SUM(c.total) as chiffreAffaires
        FROM Commande c
        WHERE c.createdAt BETWEEN :start AND :end
        GROUP BY DATE(c.createdAt)
        ORDER BY DATE(c.createdAt) DESC
        """)
    List<Object[]> getStatsByPeriod(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    @Query("""
        SELECT c.tableNum, COUNT(c), SUM(c.total)
        FROM Commande c
        WHERE DATE(c.createdAt) = :date
        GROUP BY c.tableNum
        ORDER BY COUNT(c) DESC
        """)
    List<Object[]> getTopTables(@Param("date") LocalDate date);

    interface StatsProjection {
        Long getTotal();
        Long getEnAttente();
        Long getPreparation();
        Long getPrete();
        Double getChiffreAffaires();
    }
}