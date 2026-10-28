package com.bufinder.web.service;

import com.bufinder.web.dto.ItemFormDto;
import com.bufinder.web.model.Item;
import com.bufinder.web.model.ItemStatus;
import com.bufinder.web.model.ItemType;
import com.bufinder.web.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item createItem(ItemFormDto formDto, User user) throws IOException;
    Item updateItem(Long id, ItemFormDto formDto, User currentUser) throws IOException;
    void deleteItem(Long id, User currentUser);
    Optional<Item> findById(Long id);

    Page<Item> searchItems(ItemType type, String category, ItemStatus status, String location, String keyword, Pageable pageable);
    List<Item> findRecentItems();
    List<Item> findRecentItemsByType(ItemType type, ItemStatus status);
    List<Item> findSimilarItems(String category, Long excludeId);
    List<Item> findUserItems(User user);
    Page<Item> findUserItems(User user, Pageable pageable);

    Item updateStatus(Long itemId, ItemStatus status, User currentUser);

    long countByType(ItemType type);
    long countByStatus(ItemStatus status);
    long countByTypeAndStatus(ItemType type, ItemStatus status);
    long countTotal();

    ItemFormDto toDto(Item item);
}
