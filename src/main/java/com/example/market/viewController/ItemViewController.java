package com.example.market.viewController;

import com.example.market.api.controller.comment.response.CommentResponse;
import com.example.market.api.controller.item.response.ItemResponse;
import com.example.market.service.comment.CommentService;
import com.example.market.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/views")
@Controller
public class ItemViewController {

    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping("/items")
    public String readItemList(@RequestParam(value = "page", defaultValue = "0") Integer page,
                               @RequestParam(value = "limit", defaultValue = "20") Integer limit,
                               Model model) {

        Page<ItemResponse> itemListResponseDto = itemService.readItemList(page, limit);

        model.addAttribute("itemList", itemListResponseDto);
        return "itemList";
    }

    @GetMapping("/items/{itemId}")
    public String readItemOne(@PathVariable Long itemId, Model model) {
        ItemResponse itemOneResponseDto = itemService.readItemOne(itemId);
        System.out.println("itemOneResponseDto.getId() = " + itemOneResponseDto.getId());

//        Page<CommentListResponseDto> commentListResponseDto = commentService.readCommentList(itemId, 0, 20);
        Page<CommentResponse> commentListResponseDto = commentService.readCommentList(itemId, 0, 20);

        model.addAttribute("item", itemOneResponseDto);
        model.addAttribute("commentList", commentListResponseDto);

        return "item";
    }

    @GetMapping("/item")
    public String itemForm() {
        return "newItem";
    }
}
