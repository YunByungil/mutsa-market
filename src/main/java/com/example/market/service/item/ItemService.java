package com.example.market.service.item;

import com.example.market.api.controller.item.request.ItemStatusUpdateRequest;
import com.example.market.domain.item.Item;
import com.example.market.domain.user.User;
import com.example.market.api.controller.item.request.ItemCreateRequestDto;
import com.example.market.api.controller.item.request.ItemUpdateRequestDto;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.domain.item.ItemRepository;
import com.example.market.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.market.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemResponse create(final ItemCreateRequestDto request, final Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        Item item = itemRepository.save(request.toEntity(user));

        return ItemResponse.of(item);
    }

    public Page<ItemResponse> readItemList(final int page, final int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        Page<Item> itemList = itemRepository.findAll(pageable);

        Page<ItemResponse> result = itemList.map(ItemResponse::of);

        return result;
    }

    public Page<ItemResponse> readItemListTest(final Long userId, final int page, final int limit) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));
        Page<Item> itemList = itemRepository.customFindAllByDistance(pageable, user.getLocation(), user.getSearchScope().getScope());
//        Page<Item> itemList = itemRepository.customFindAllByDistance(pageable, user.getLocation());

        Page<ItemResponse> result = itemList.map(ItemResponse::of);

        return result;
    }

    public ItemResponse readItemOne(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, ItemUpdateRequestDto dto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        item.update(dto);
        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse deleteItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        itemRepository.delete(item);
        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateItemImage(Long itemId, MultipartFile image, Long userId) throws IOException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        // TODO: 해결해야 됨
//        checkWriterAndPassword(writer, password, item);

        // 파일 어디에 업로드? -> media/{userId}/profile.{파일 확장자}
        String profileDir = String.format("media/%d/", itemId);
        try {
            // 폴더만 만드는 과정
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new MarketAppException(SERVER_ERROR, SERVER_ERROR.getMessage());
        }

        // 확장자를 포함한 이미지 이름 만들기 profile.{확장자}
        String originalFilename = image.getOriginalFilename();

        // img.jpg -> fileNameSplit = {"img", "jpg"}
        String[] fileNameSplit = originalFilename.split("\\.");

        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "profile." +extension;

        // 폴더와 파일 경로를 포함한 이름 만들기
        String profilePath = profileDir + profileFilename;
        Path path = Path.of(profilePath);

        // MultipartFile 저장하기
        try {
            image.transferTo(path);
        } catch (IOException e) {
            e.getMessage();
            throw new MarketAppException(SERVER_ERROR, SERVER_ERROR.getMessage());
        }


        // itemUpdate
        item.updateItemImage(String.format("/static/%d/%s", itemId, profileFilename));

        return ItemResponse.of(item);
    }

    @Transactional
    public ItemResponse updateItemStatus(final Long itemId, final ItemStatusUpdateRequest request, final Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_ITEM, NOT_FOUND_ITEM.getMessage()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new MarketAppException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new MarketAppException(INVALID_WRITER, INVALID_WRITER.getMessage());
        }

        item.updateStatus(request.getStatus());

        return ItemResponse.of(item);
    }
}
