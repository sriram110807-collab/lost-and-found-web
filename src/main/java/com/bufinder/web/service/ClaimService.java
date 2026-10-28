package com.bufinder.web.service;

import com.bufinder.web.dto.ClaimFormDto;
import com.bufinder.web.model.Claim;
import com.bufinder.web.model.ClaimStatus;
import com.bufinder.web.model.User;

import java.util.List;
import java.util.Optional;

public interface ClaimService {
    Claim submitClaim(ClaimFormDto claimDto, User claimant);
    Optional<Claim> findById(Long id);
    List<Claim> findClaimsByClaimant(User claimant);
    List<Claim> findClaimsReceivedForUser(User itemOwner);
    List<Claim> findAllClaims();
    Claim updateClaimStatus(Long claimId, ClaimStatus status, String reviewerNotes, User currentUser);
    long countClaimsByStatus(ClaimStatus status);
    long countTotalClaims();
}
