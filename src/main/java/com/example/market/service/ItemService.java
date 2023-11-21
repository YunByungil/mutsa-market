package com.example.market.service;

import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.item.request.ItemCreateRequestDto;
import com.example.market.dto.item.request.ItemUpdateRequestDto;
import com.example.market.dto.item.response.ItemListResponseDto;
import com.example.market.dto.item.response.ItemOneResponseDto;
import com.example.market.dto.item.response.ItemResponse;
import com.example.market.exception.MarketAppException;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        Page<Item> itemList = itemRepository.findAll(pageable);

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (item.getUser().getId() != user.getId()) {
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

}
