package com.bufinder.web.repository;

import com.bufinder.web.model.Item;
import com.bufinder.web.model.ItemStatus;
import com.bufinder.web.model.ItemType;
import com.bufinder.web.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserOrderByCreatedAtDesc(User user);

    Page<Item> findByUser(User user, Pageable pageable);

    List<Item> findTop6ByOrderByCreatedAtDesc();

    List<Item> findTop4ByTypeAndStatusOrderByCreatedAtDesc(ItemType type, ItemStatus status);

    List<Item> findTop4ByCategoryAndIdNot(String category, Long id);

    long countByType(ItemType type);

    long countByStatus(ItemStatus status);

    long countByTypeAndStatus(ItemType type, ItemStatus status);

    @Query("SELECT i FROM Item i WHERE " +
           "(:type IS NULL OR i.type = :type) AND " +
           "(:category IS NULL OR :category = '' OR i.category = :category) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:location IS NULL OR :location = '' OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.subType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.color) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Item> searchItems(
            @Param("type") ItemType type,
            @Param("category") String category,
            @Param("status") ItemStatus status,
            @Param("location") String location,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
