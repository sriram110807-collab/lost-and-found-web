package com.bufinder.web.service;

import com.bufinder.web.dto.ClaimFormDto;
import com.bufinder.web.model.*;
import com.bufinder.web.repository.ClaimRepository;
import com.bufinder.web.repository.ItemRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    public ClaimServiceImpl(ClaimRepository claimRepository, ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Claim submitClaim(ClaimFormDto claimDto, User claimant) {
        Item item = itemRepository.findById(claimDto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + claimDto.getItemId()));

        if (item.getUser().getId().equals(claimant.getId())) {
            throw new IllegalArgumentException("You cannot submit a claim on an item that you reported.");
        }

        if (item.getStatus() == ItemStatus.RESOLVED || item.getStatus() == ItemStatus.CLOSED) {
            throw new IllegalArgumentException("This item report has already been resolved or closed.");
        }

        Claim claim = new Claim(
                item,
                claimant,
                claimDto.getClaimantName().trim(),
                claimDto.getClaimantPhone() != null ? claimDto.getClaimantPhone().trim() : null,
                claimDto.getClaimantEmail().trim(),
                claimDto.getProofDescription().trim()
        );

        // Update item status to CLAIM_PENDING if currently ACTIVE
        if (item.getStatus() == ItemStatus.ACTIVE) {
            item.setStatus(ItemStatus.CLAIM_PENDING);
            itemRepository.save(item);
        }

        return claimRepository.save(claim);
    }

    @Override
    public Optional<Claim> findById(Long id) {
        if (id == null) return Optional.empty();
        return claimRepository.findById(id);
    }

    @Override
    public List<Claim> findClaimsByClaimant(User claimant) {
        return claimRepository.findByClaimantOrderByClaimDateDesc(claimant);
    }

    @Override
    public List<Claim> findClaimsReceivedForUser(User itemOwner) {
        return claimRepository.findByItemUserOrderByClaimDateDesc(itemOwner);
    }

    @Override
    public List<Claim> findAllClaims() {
        return claimRepository.findAllByOrderByClaimDateDesc();
    }

    @Override
    public Claim updateClaimStatus(Long claimId, ClaimStatus status, String reviewerNotes, User currentUser) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found with ID: " + claimId));

        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = claim.getItem().getUser().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to update this claim status.");
        }

        claim.setStatus(status);
        claim.setReviewerNotes(reviewerNotes != null ? reviewerNotes.trim() : null);
        claim.setReviewedAt(LocalDateTime.now());

        // If approved, mark the item as RESOLVED!
        if (status == ClaimStatus.APPROVED) {
            Item item = claim.getItem();
            item.setStatus(ItemStatus.RESOLVED);
            itemRepository.save(item);
        } else if (status == ClaimStatus.REJECTED) {
            // Check if there are other pending claims for this item
            List<Claim> otherClaims = claimRepository.findByItemOrderByClaimDateDesc(claim.getItem());
            boolean hasOtherPending = otherClaims.stream()
                    .anyMatch(c -> !c.getId().equals(claim.getId()) && c.getStatus() == ClaimStatus.PENDING);
            if (!hasOtherPending && claim.getItem().getStatus() == ItemStatus.CLAIM_PENDING) {
                claim.getItem().setStatus(ItemStatus.ACTIVE);
                itemRepository.save(claim.getItem());
            }
        }

        return claimRepository.save(claim);
    }

    @Override
    public long countClaimsByStatus(ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }

    @Override
    public long countTotalClaims() {
        return claimRepository.count();
    }
}
