package com.bufinder.web.controller;

import com.bufinder.web.model.ItemStatus;
import com.bufinder.web.model.ItemType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicPagesAccessibleByAnonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/browse"));

        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));

        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact"));

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));
    }

    @Test
    void testProtectedDashboardRedirectsAnonymousToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "demo@bufinder.com", roles = {"USER"})
    void testUserCanAccessDashboardAndReportPages() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/user"));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/profile"));

        mockMvc.perform(get("/items/report-lost"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/report-lost"));

        mockMvc.perform(get("/items/report-found"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/report-found"));
    }

    @Test
    @WithMockUser(username = "demo@bufinder.com", roles = {"USER"})
    void testNonAdminForbiddenFromAdminPortal() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@bufinder.com", roles = {"ADMIN"})
    void testAdminCanAccessAdminPortal() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));

        mockMvc.perform(get("/admin/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/items"));

        mockMvc.perform(get("/admin/claims"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/claims"));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"));
    }

    @Test
    @WithMockUser(username = "demo@bufinder.com", roles = {"USER"})
    void testSubmitLostItemReport() throws Exception {
        mockMvc.perform(post("/items/report-lost")
                .with(csrf())
                .param("title", "Integration Test Lost Calculator")
                .param("type", "LOST")
                .param("category", "Books and Stationery")
                .param("subType", "Calculator")
                .param("brand", "Casio (Calculator)")
                .param("color", "Black")
                .param("location", "Room 201")
                .param("dateEvent", "2026-10-25")
                .param("description", "Scientific calculator fx-991EX")
                .param("identifyingDetails", "Name inscribed on back lid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/items/*"));
    }
}
