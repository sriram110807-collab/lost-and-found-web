package com.bufinder.web.service;

import com.bufinder.web.dto.ItemFormDto;
import com.bufinder.web.model.Item;
import com.bufinder.web.model.ItemStatus;
import com.bufinder.web.model.ItemType;
import com.bufinder.web.model.Role;
import com.bufinder.web.model.User;
import com.bufinder.web.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final FileUploadService fileUploadService;

    public ItemServiceImpl(ItemRepository itemRepository, FileUploadService fileUploadService) {
        this.itemRepository = itemRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public Item createItem(ItemFormDto formDto, User user) throws IOException {
        String imagePath = null;
        if (formDto.getImageFile() != null && !formDto.getImageFile().isEmpty()) {
            imagePath = fileUploadService.saveFile(formDto.getImageFile());
        }

        Item item = new Item(
                formDto.getTitle().trim(),
                formDto.getType(),
                formDto.getCategory().trim(),
                formDto.getSubType() != null ? formDto.getSubType().trim() : null,
                formDto.getBrand() != null ? formDto.getBrand().trim() : null,
                formDto.getColor() != null ? formDto.getColor().trim() : null,
                formDto.getLocation().trim(),
                formDto.getDateEvent(),
                formDto.getDescription() != null ? formDto.getDescription().trim() : null,
                formDto.getIdentifyingDetails() != null ? formDto.getIdentifyingDetails().trim() : null,
                imagePath,
                user
        );

        if (formDto.getStatus() != null) {
            item.setStatus(formDto.getStatus());
        }

        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long id, ItemFormDto formDto, User currentUser) throws IOException {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));

        // Ownership check
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = item.getUser().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to update this item report.");
        }

        item.setTitle(formDto.getTitle().trim());
        item.setType(formDto.getType());
        item.setCategory(formDto.getCategory().trim());
        item.setSubType(formDto.getSubType() != null ? formDto.getSubType().trim() : null);
        item.setBrand(formDto.getBrand() != null ? formDto.getBrand().trim() : null);
        item.setColor(formDto.getColor() != null ? formDto.getColor().trim() : null);
        item.setLocation(formDto.getLocation().trim());
        item.setDateEvent(formDto.getDateEvent());
        item.setDescription(formDto.getDescription() != null ? formDto.getDescription().trim() : null);
        item.setIdentifyingDetails(formDto.getIdentifyingDetails() != null ? formDto.getIdentifyingDetails().trim() : null);

        if (formDto.getStatus() != null) {
            item.setStatus(formDto.getStatus());
        }

        // New image uploaded?
        if (formDto.getImageFile() != null && !formDto.getImageFile().isEmpty()) {
            if (item.getImagePath() != null) {
                fileUploadService.deleteFile(item.getImagePath());
            }
            String newImagePath = fileUploadService.saveFile(formDto.getImageFile());
            item.setImagePath(newImagePath);
        }

        return itemRepository.save(item);
    }

    @Override
    public void deleteItem(Long id, User currentUser) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + id));

        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = item.getUser().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to delete this item report.");
        }

        if (item.getImagePath() != null) {
            fileUploadService.deleteFile(item.getImagePath());
        }

        itemRepository.delete(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (id == null) return Optional.empty();
        return itemRepository.findById(id);
    }

    @Override
    public Page<Item> searchItems(ItemType type, String category, ItemStatus status, String location, String keyword, Pageable pageable) {
        return itemRepository.searchItems(type, category, status, location, keyword, pageable);
    }

    @Override
    public List<Item> findRecentItems() {
        return itemRepository.findTop6ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Item> findRecentItemsByType(ItemType type, ItemStatus status) {
        return itemRepository.findTop4ByTypeAndStatusOrderByCreatedAtDesc(type, status);
    }

    @Override
    public List<Item> findSimilarItems(String category, Long excludeId) {
        if (category == null || excludeId == null) {
            return List.of();
        }
        return itemRepository.findTop4ByCategoryAndIdNot(category, excludeId);
    }

    @Override
    public List<Item> findUserItems(User user) {
        return itemRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public Page<Item> findUserItems(User user, Pageable pageable) {
        return itemRepository.findByUser(user, pageable);
    }

    @Override
    public Item updateStatus(Long itemId, ItemStatus status, User currentUser) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
        boolean isOwner = item.getUser().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not authorized to modify the status of this item.");
        }

        item.setStatus(status);
        return itemRepository.save(item);
    }

    @Override
    public long countByType(ItemType type) {
        return itemRepository.countByType(type);
    }

    @Override
    public long countByStatus(ItemStatus status) {
        return itemRepository.countByStatus(status);
    }

    @Override
    public long countByTypeAndStatus(ItemType type, ItemStatus status) {
        return itemRepository.countByTypeAndStatus(type, status);
    }

    @Override
    public long countTotal() {
        return itemRepository.count();
    }

    @Override
    public ItemFormDto toDto(Item item) {
        ItemFormDto dto = new ItemFormDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setType(item.getType());
        dto.setCategory(item.getCategory());
        dto.setSubType(item.getSubType());
        dto.setBrand(item.getBrand());
        dto.setColor(item.getColor());
        dto.setLocation(item.getLocation());
        dto.setDateEvent(item.getDateEvent());
        dto.setDescription(item.getDescription());
        dto.setIdentifyingDetails(item.getIdentifyingDetails());
        dto.setExistingImagePath(item.getImagePath());
        dto.setStatus(item.getStatus());
        return dto;
    }
}
