package com.bufinder.web.controller;

import com.bufinder.web.dto.ClaimFormDto;
import com.bufinder.web.dto.ItemFormDto;
import com.bufinder.web.model.*;
import com.bufinder.web.service.CategoryDataService;
import com.bufinder.web.service.ClaimService;
import com.bufinder.web.service.ItemService;
import com.bufinder.web.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;
    private final ClaimService claimService;
    private final CategoryDataService categoryDataService;

    public ItemController(ItemService itemService, UserService userService,
                          ClaimService claimService, CategoryDataService categoryDataService) {
        this.itemService = itemService;
        this.userService = userService;
        this.claimService = claimService;
        this.categoryDataService = categoryDataService;
    }

    @GetMapping
    public String browseItems(@RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "type", required = false) ItemType type,
                              @RequestParam(name = "category", required = false) String category,
                              @RequestParam(name = "status", required = false) ItemStatus status,
                              @RequestParam(name = "location", required = false) String location,
                              @RequestParam(name = "sort", defaultValue = "createdAt,desc") String sort,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "9") int size,
                              Model model) {

        String[] sortParams = sort.split(",");
        Sort sorting = Sort.by(
                sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortParams[0]
        );

        Pageable pageable = PageRequest.of(Math.max(0, page), size, sorting);
        Page<Item> itemPage = itemService.searchItems(type, category, status, location, keyword, pageable);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", itemPage.getNumber());
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalElements", itemPage.getTotalElements());

        // Pass filter attributes for active search retention in UI
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedLocation", location);
        model.addAttribute("selectedSort", sort);

        // Reference lists
        model.addAttribute("categories", categoryDataService.getCategories());
        model.addAttribute("allStatuses", ItemStatus.values());
        model.addAttribute("allTypes", ItemType.values());

        return "items/browse";
    }

    @GetMapping("/{id:[0-9]+}")
    public String itemDetails(@PathVariable("id") Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Optional<Item> itemOpt = itemService.findById(id);
        if (itemOpt.isEmpty()) {
            return "redirect:/items";
        }

        Item item = itemOpt.get();
        model.addAttribute("item", item);

        // Determine if current visitor is the reporter or admin
        boolean isOwner = false;
        boolean isAdmin = false;
        User currentUser = null;

        if (userDetails != null) {
            currentUser = userService.findByEmail(userDetails.getUsername()).orElse(null);
            if (currentUser != null) {
                isOwner = item.getUser().getId().equals(currentUser.getId());
                isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
            }
        }

        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUser", currentUser);

        // Pre-fill claim form DTO
        if (!model.containsAttribute("claimForm")) {
            ClaimFormDto claimForm = new ClaimFormDto();
            claimForm.setItemId(item.getId());
            if (currentUser != null) {
                claimForm.setClaimantName(currentUser.getFullName());
                claimForm.setClaimantEmail(currentUser.getEmail());
                claimForm.setClaimantPhone(currentUser.getPhone());
            }
            model.addAttribute("claimForm", claimForm);
        }

        // Similar items
        List<Item> similarItems = itemService.findSimilarItems(item.getCategory(), item.getId());
        model.addAttribute("similarItems", similarItems);

        return "items/details";
    }

    @GetMapping("/report-lost")
    public String reportLostForm(Model model) {
        if (!model.containsAttribute("itemForm")) {
            ItemFormDto formDto = new ItemFormDto();
            formDto.setType(ItemType.LOST);
            model.addAttribute("itemForm", formDto);
        }
        populateFormData(model);
        return "items/report-lost";
    }

    @PostMapping("/report-lost")
    public String submitReportLost(@Valid @ModelAttribute("itemForm") ItemFormDto formDto,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        formDto.setType(ItemType.LOST);

        if (bindingResult.hasErrors()) {
            populateFormData(model);
            return "items/report-lost";
        }

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        try {
            Item created = itemService.createItem(formDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Your lost item report has been published successfully!");
            return "redirect:/items/" + created.getId();
        } catch (IllegalArgumentException | IOException e) {
            bindingResult.reject("uploadError", e.getMessage());
            populateFormData(model);
            return "items/report-lost";
        }
    }

    @GetMapping("/report-found")
    public String reportFoundForm(Model model) {
        if (!model.containsAttribute("itemForm")) {
            ItemFormDto formDto = new ItemFormDto();
            formDto.setType(ItemType.FOUND);
            model.addAttribute("itemForm", formDto);
        }
        populateFormData(model);
        return "items/report-found";
    }

    @PostMapping("/report-found")
    public String submitReportFound(@Valid @ModelAttribute("itemForm") ItemFormDto formDto,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        formDto.setType(ItemType.FOUND);

        if (bindingResult.hasErrors()) {
            populateFormData(model);
            return "items/report-found";
        }

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        try {
            Item created = itemService.createItem(formDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you! Your found item report has been published successfully.");
            return "redirect:/items/" + created.getId();
        } catch (IllegalArgumentException | IOException e) {
            bindingResult.reject("uploadError", e.getMessage());
            populateFormData(model);
            return "items/report-found";
        }
    }

    @GetMapping("/{id:[0-9]+}/edit")
    public String editItemForm(@PathVariable("id") Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        Item item = itemService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = item.getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to edit this report.");
        }

        if (!model.containsAttribute("itemForm")) {
            ItemFormDto formDto = itemService.toDto(item);
            model.addAttribute("itemForm", formDto);
        }

        model.addAttribute("item", item);
        populateFormData(model);
        return "items/edit";
    }

    @PostMapping("/{id:[0-9]+}/edit")
    public String updateItem(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("itemForm") ItemFormDto formDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        if (bindingResult.hasErrors()) {
            Item item = itemService.findById(id).orElse(null);
            model.addAttribute("item", item);
            populateFormData(model);
            return "items/edit";
        }

        try {
            itemService.updateItem(id, formDto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Item report updated successfully.");
            return "redirect:/items/" + id;
        } catch (IllegalArgumentException | IOException e) {
            bindingResult.reject("error", e.getMessage());
            Item item = itemService.findById(id).orElse(null);
            model.addAttribute("item", item);
            populateFormData(model);
            return "items/edit";
        }
    }

    @PostMapping("/{id:[0-9]+}/delete")
    public String deleteItem(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        try {
            itemService.deleteItem(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Item report deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/{id:[0-9]+}/claim")
    public String submitClaim(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("claimForm") ClaimFormDto claimDto,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide complete proof of ownership.");
            return "redirect:/items/" + id;
        }

        User claimant = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        try {
            claimDto.setItemId(id);
            claimService.submitClaim(claimDto, claimant);
            redirectAttributes.addFlashAttribute("successMessage", "Your claim request has been submitted to the finder/reporter for verification!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/items/" + id;
    }

    private void populateFormData(Model model) {
        model.addAttribute("categories", categoryDataService.getCategories());
        model.addAttribute("colors", categoryDataService.getColors());
        model.addAttribute("allStatuses", ItemStatus.values());
    }
}
