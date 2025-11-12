package com.freshfold.repository;

import com.freshfold.model.LaundryOrder;
import com.freshfold.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<LaundryOrder, Long> {

    // Find orders by student
    List<LaundryOrder> findByStudentId(Long studentId);
    List<LaundryOrder> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    // Find orders by personnel
    List<LaundryOrder> findByPersonnelId(Long personnelId);
    List<LaundryOrder> findByPersonnelIdAndStatus(Long personnelId, OrderStatus status);
    List<LaundryOrder> findByPersonnelIdAndStatusIn(Long personnelId, List<OrderStatus> statuses);

    // Find orders by status
    List<LaundryOrder> findByStatus(OrderStatus status);
    List<LaundryOrder> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    // Get recent orders
    List<LaundryOrder> findTop10ByOrderByCreatedAtDesc();

    // Count orders
    Long countByStatus(OrderStatus status);
    Long countByPersonnelIdAndStatus(Long personnelId, OrderStatus status);

    // Calculate revenue
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM LaundryOrder o WHERE o.status = 'DONE'")
    Double calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM LaundryOrder o " +
            "WHERE o.personnel.id = :personnelId AND o.status = 'DONE'")
    Double calculatePersonnelRevenue(@Param("personnelId") Long personnelId);

    // Statistics queries
    @Query("SELECT s.hostel, COUNT(o) FROM LaundryOrder o " +
            "JOIN o.student s GROUP BY s.hostel")
    List<Object[]> getHostelWiseOrderCount();

    @Query("SELECT s.hostel, COALESCE(SUM(o.totalPrice), 0) FROM LaundryOrder o " +
            "JOIN o.student s WHERE o.status = 'DONE' GROUP BY s.hostel")
    List<Object[]> getHostelWiseRevenue();

    @Query("SELECT p.fullName, COUNT(o), COALESCE(SUM(o.totalPrice), 0), p.rating " +
            "FROM LaundryOrder o JOIN o.personnel p " +
            "WHERE o.status = 'DONE' GROUP BY p.id, p.fullName, p.rating")
    List<Object[]> getPersonnelPerformance();
}