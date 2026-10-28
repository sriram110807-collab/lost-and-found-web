package com.bufinder.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClaimFormDto {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Your name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String claimantName;

    private String claimantPhone;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Please provide a valid email address")
    private String claimantEmail;

    @NotBlank(message = "Please provide description / proof of ownership")
    @Size(min = 10, message = "Proof description should be at least 10 characters")
    private String proofDescription;

    public ClaimFormDto() {
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getClaimantPhone() {
        return claimantPhone;
    }

    public void setClaimantPhone(String claimantPhone) {
        this.claimantPhone = claimantPhone;
    }

    public String getClaimantEmail() {
        return claimantEmail;
    }

    public void setClaimantEmail(String claimantEmail) {
        this.claimantEmail = claimantEmail;
    }

    public String getProofDescription() {
        return proofDescription;
    }

    public void setProofDescription(String proofDescription) {
        this.proofDescription = proofDescription;
    }
}
