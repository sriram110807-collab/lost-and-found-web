package com.bufinder.web.service;

import com.bufinder.web.dto.ClaimFormDto;
import com.bufinder.web.model.*;
import com.bufinder.web.repository.ClaimRepository;
import com.bufinder.web.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private User reporter;
    private User claimant;
    private Item item;
    private ClaimFormDto claimDto;

    @BeforeEach
    void setUp() {
        reporter = new User("Reporter", "reporter@bufinder.com", "pass", "111", Role.ROLE_USER);
        reporter.setId(1L);

        claimant = new User("Claimant", "claimant@bufinder.com", "pass", "222", Role.ROLE_USER);
        claimant.setId(2L);

        item = new Item("Found Watch", ItemType.FOUND, "Wearables", "Watches", "Titan", "Brown",
                "Cafeteria", LocalDate.now(), "Found watch", "Secret engraving", null, reporter);
        item.setId(50L);

        claimDto = new ClaimFormDto();
        claimDto.setItemId(50L);
        claimDto.setClaimantName("Claimant");
        claimDto.setClaimantEmail("claimant@bufinder.com");
        claimDto.setClaimantPhone("222");
        claimDto.setProofDescription("The back has an engraving of initials 'RS 2023'");
    }

    @Test
    void testSubmitClaim_Success() {
        when(itemRepository.findById(50L)).thenReturn(Optional.of(item));
        when(claimRepository.save(any(Claim.class))).thenAnswer(i -> {
            Claim c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        Claim submitted = claimService.submitClaim(claimDto, claimant);

        assertNotNull(submitted);
        assertEquals(ClaimStatus.PENDING, submitted.getStatus());
        assertEquals(ItemStatus.CLAIM_PENDING, item.getStatus());
        verify(itemRepository, times(1)).save(item);
        verify(claimRepository, times(1)).save(any(Claim.class));
    }

    @Test
    void testSubmitClaim_SelfClaimThrowsException() {
        when(itemRepository.findById(50L)).thenReturn(Optional.of(item));

        // reporter tries to claim their own item
        assertThrows(IllegalArgumentException.class, () -> claimService.submitClaim(claimDto, reporter));
        verify(claimRepository, never()).save(any(Claim.class));
    }

    @Test
    void testUpdateClaimStatus_ApproveMarksItemResolved() {
        Claim claim = new Claim(item, claimant, "Claimant", "222", "claimant@bufinder.com", "Proof details");
        claim.setId(10L);

        when(claimRepository.findById(10L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenReturn(claim);

        Claim updated = claimService.updateClaimStatus(10L, ClaimStatus.APPROVED, "Verified proof matched", reporter);

        assertEquals(ClaimStatus.APPROVED, updated.getStatus());
        assertEquals(ItemStatus.RESOLVED, item.getStatus());
        verify(itemRepository, times(1)).save(item);
    }
}
