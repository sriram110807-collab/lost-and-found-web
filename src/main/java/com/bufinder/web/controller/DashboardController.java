package com.bufinder.web.controller;

import com.bufinder.web.dto.UserProfileDto;
import com.bufinder.web.model.*;
import com.bufinder.web.service.ClaimService;
import com.bufinder.web.service.ItemService;
import com.bufinder.web.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ItemService itemService;
    private final ClaimService claimService;

    public DashboardController(UserService userService, ItemService itemService, ClaimService claimService) {
        this.userService = userService;
        this.itemService = itemService;
        this.claimService = claimService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(name = "tab", defaultValue = "items") String activeTab,
                            Model model) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Item> myItems = itemService.findUserItems(currentUser);
        List<Claim> mySentClaims = claimService.findClaimsByClaimant(currentUser);
        List<Claim> claimsReceived = claimService.findClaimsReceivedForUser(currentUser);

        long myActiveReports = myItems.stream().filter(i -> i.getStatus() == ItemStatus.ACTIVE).count();
        long myResolvedReports = myItems.stream().filter(i -> i.getStatus() == ItemStatus.RESOLVED).count();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myItems", myItems);
        model.addAttribute("mySentClaims", mySentClaims);
        model.addAttribute("claimsReceived", claimsReceived);
        model.addAttribute("activeTab", activeTab);

        model.addAttribute("myTotalReports", myItems.size());
        model.addAttribute("myActiveReports", myActiveReports);
        model.addAttribute("myResolvedReports", myResolvedReports);
        model.addAttribute("myPendingClaimsCount", claimsReceived.stream().filter(c -> c.getStatus() == ClaimStatus.PENDING).count());

        return "dashboard/user";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!model.containsAttribute("profileDto")) {
            UserProfileDto dto = new UserProfileDto();
            dto.setFullName(currentUser.getFullName());
            dto.setEmail(currentUser.getEmail());
            dto.setPhone(currentUser.getPhone());
            model.addAttribute("profileDto", dto);
        }

        model.addAttribute("currentUser", currentUser);
        return "dashboard/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDto") UserProfileDto profileDto,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", currentUser);
            return "dashboard/profile";
        }

        try {
            userService.updateProfile(currentUser.getId(), profileDto);

            // Check if password change was requested
            if (profileDto.getNewPassword() != null && !profileDto.getNewPassword().isBlank()) {
                if (profileDto.getCurrentPassword() == null || profileDto.getCurrentPassword().isBlank()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Current password is required to change password.");
                    return "redirect:/profile";
                }
                if (!profileDto.getNewPassword().equals(profileDto.getConfirmNewPassword())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "New passwords do not match.");
                    return "redirect:/profile";
                }
                if (profileDto.getNewPassword().length() < 6) {
                    redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 6 characters.");
                    return "redirect:/profile";
                }

                boolean changed = userService.changePassword(currentUser.getId(), profileDto.getCurrentPassword(), profileDto.getNewPassword());
                if (!changed) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Incorrect current password. Password was not changed.");
                    return "redirect:/profile";
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/claims/{id:[0-9]+}/status")
    public String updateClaimStatus(@PathVariable("id") Long claimId,
                                    @RequestParam("status") ClaimStatus status,
                                    @RequestParam(name = "reviewerNotes", required = false) String notes,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        try {
            claimService.updateClaimStatus(claimId, status, notes, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Claim status updated to: " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard?tab=received";
    }
}
