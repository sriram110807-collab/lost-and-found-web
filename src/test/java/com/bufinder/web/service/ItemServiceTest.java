package com.bufinder.web.service;

import com.bufinder.web.dto.ItemFormDto;
import com.bufinder.web.model.*;
import com.bufinder.web.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private User otherUser;
    private User admin;
    private Item item;
    private ItemFormDto formDto;

    @BeforeEach
    void setUp() {
        owner = new User("Owner", "owner@bufinder.com", "pass", "123", Role.ROLE_USER);
        owner.setId(1L);

        otherUser = new User("Other", "other@bufinder.com", "pass", "456", Role.ROLE_USER);
        otherUser.setId(2L);

        admin = new User("Admin", "admin@bufinder.com", "pass", "789", Role.ROLE_ADMIN);
        admin.setId(3L);

        item = new Item("Lost Keys", ItemType.LOST, "Keys", "Room Keys", "Godrej", "Silver",
                "Hostel Block C", LocalDate.now(), "Keys with tag", "Secret room number 304", null, owner);
        item.setId(100L);

        formDto = new ItemFormDto();
        formDto.setTitle("Found Watch");
        formDto.setType(ItemType.FOUND);
        formDto.setCategory("Wearables");
        formDto.setSubType("Watches");
        formDto.setBrand("Titan");
        formDto.setColor("Brown");
        formDto.setLocation("Cafeteria");
        formDto.setDateEvent(LocalDate.now());
        formDto.setDescription("Found brown watch");
        formDto.setIdentifyingDetails("Engraving on back");
    }

    @Test
    void testCreateItem_Success() throws IOException {
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item saved = invocation.getArgument(0);
            saved.setId(101L);
            return saved;
        });

        Item created = itemService.createItem(formDto, owner);

        assertNotNull(created);
        assertEquals(101L, created.getId());
        assertEquals("Found Watch", created.getTitle());
        assertEquals(ItemType.FOUND, created.getType());
        assertEquals(ItemStatus.ACTIVE, created.getStatus());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testUpdateItem_OwnerCanUpdate() throws IOException {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        formDto.setTitle("Updated Title");
        Item updated = itemService.updateItem(100L, formDto, owner);

        assertEquals("Updated Title", updated.getTitle());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItem_AdminCanUpdate() throws IOException {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        formDto.setTitle("Admin Updated Title");
        Item updated = itemService.updateItem(100L, formDto, admin);

        assertEquals("Admin Updated Title", updated.getTitle());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItem_UnauthorizedUserThrowsAccessDenied() {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(100L, formDto, otherUser));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testDeleteItem_OwnerCanDelete() {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        itemService.deleteItem(100L, owner);
        verify(itemRepository, times(1)).delete(item);
    }

    @Test
    void testDeleteItem_UnauthorizedUserThrowsAccessDenied() {
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> itemService.deleteItem(100L, otherUser));
        verify(itemRepository, never()).delete(any(Item.class));
    }
}
