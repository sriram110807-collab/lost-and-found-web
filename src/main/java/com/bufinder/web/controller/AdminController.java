package com.bufinder.web.controller;

import com.bufinder.web.model.*;
import com.bufinder.web.service.ClaimService;
import com.bufinder.web.service.ItemService;
import com.bufinder.web.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ItemService itemService;
    private final ClaimService claimService;

    public AdminController(UserService userService, ItemService itemService, ClaimService claimService) {
        this.userService = userService;
        this.itemService = itemService;
        this.claimService = claimService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("totalUsers", userService.getTotalUserCount());
        model.addAttribute("totalItems", itemService.countTotal());
        model.addAttribute("activeLost", itemService.countByTypeAndStatus(ItemType.LOST, ItemStatus.ACTIVE));
        model.addAttribute("activeFound", itemService.countByTypeAndStatus(ItemType.FOUND, ItemStatus.ACTIVE));
        model.addAttribute("resolvedItems", itemService.countByStatus(ItemStatus.RESOLVED));
        model.addAttribute("pendingClaims", claimService.countClaimsByStatus(ClaimStatus.PENDING));

        model.addAttribute("recentItems", itemService.findRecentItems());
        model.addAttribute("allClaims", claimService.findAllClaims());

        return "admin/dashboard";
    }

    @GetMapping("/items")
    public String manageItems(@RequestParam(name = "type", required = false) ItemType type,
                              @RequestParam(name = "status", required = false) ItemStatus status,
                              @RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, page), 15, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Item> itemPage = itemService.searchItems(type, null, status, null, keyword, pageable);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", itemPage.getNumber());
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalElements", itemPage.getTotalElements());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("allStatuses", ItemStatus.values());
        model.addAttribute("allTypes", ItemType.values());

        return "admin/items";
    }

    @PostMapping("/items/{id:[0-9]+}/status")
    public String updateItemStatus(@PathVariable("id") Long id,
                                   @RequestParam("status") ItemStatus status,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            itemService.updateStatus(id, status, admin);
            redirectAttributes.addFlashAttribute("successMessage", "Item #" + id + " status changed to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @PostMapping("/items/{id:[0-9]+}/delete")
    public String deleteItemByAdmin(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            itemService.deleteItem(id, admin);
            redirectAttributes.addFlashAttribute("successMessage", "Item #" + id + " was deleted by administrator.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/claims")
    public String manageClaims(Model model) {
        model.addAttribute("claims", claimService.findAllClaims());
        model.addAttribute("pendingCount", claimService.countClaimsByStatus(ClaimStatus.PENDING));
        return "admin/claims";
    }

    @PostMapping("/claims/{id:[0-9]+}/status")
    public String updateClaimStatusByAdmin(@PathVariable("id") Long id,
                                           @RequestParam("status") ClaimStatus status,
                                           @RequestParam(name = "reviewerNotes", required = false) String notes,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        User admin = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            claimService.updateClaimStatus(id, status, notes, admin);
            redirectAttributes.addFlashAttribute("successMessage", "Claim #" + id + " updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/claims";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }
}
