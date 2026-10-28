package com.bufinder.web.controller;

import com.bufinder.web.model.ItemStatus;
import com.bufinder.web.model.ItemType;
import com.bufinder.web.service.CategoryDataService;
import com.bufinder.web.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemService itemService;
    private final CategoryDataService categoryDataService;

    public HomeController(ItemService itemService, CategoryDataService categoryDataService) {
        this.itemService = itemService;
        this.categoryDataService = categoryDataService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recentLostItems", itemService.findRecentItemsByType(ItemType.LOST, ItemStatus.ACTIVE));
        model.addAttribute("recentFoundItems", itemService.findRecentItemsByType(ItemType.FOUND, ItemStatus.ACTIVE));
        model.addAttribute("categories", categoryDataService.getCategories());

        // Platform Statistics
        model.addAttribute("totalReports", itemService.countTotal());
        model.addAttribute("activeLost", itemService.countByTypeAndStatus(ItemType.LOST, ItemStatus.ACTIVE));
        model.addAttribute("activeFound", itemService.countByTypeAndStatus(ItemType.FOUND, ItemStatus.ACTIVE));
        model.addAttribute("resolvedItems", itemService.countByStatus(ItemStatus.RESOLVED));

        return "home";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
}
