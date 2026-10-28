package com.bufinder.web.repository;

import com.bufinder.web.model.Claim;
import com.bufinder.web.model.ClaimStatus;
import com.bufinder.web.model.Item;
import com.bufinder.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimantOrderByClaimDateDesc(User claimant);

    List<Claim> findByItemOrderByClaimDateDesc(Item item);

    List<Claim> findByItemUserOrderByClaimDateDesc(User itemOwner);

    List<Claim> findAllByOrderByClaimDateDesc();

    boolean existsByItemAndClaimant(Item item, User claimant);

    long countByStatus(ClaimStatus status);
}
