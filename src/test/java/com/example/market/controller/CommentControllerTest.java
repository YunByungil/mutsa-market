package com.example.market.controller;

import com.example.market.domain.entity.Comment;
import com.example.market.domain.entity.Item;
import com.example.market.domain.entity.enums.Role;
import com.example.market.domain.entity.user.User;
import com.example.market.dto.comment.request.CommentCreateRequestDto;
import com.example.market.dto.comment.request.CommentReplyRequestDto;
import com.example.market.dto.comment.request.CommentUpdateRequestDto;
import com.example.market.repository.CommentRepository;
import com.example.market.repository.ItemRepository;
import com.example.market.repository.user.UserRepository;
import com.example.market.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
class CommentControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    Item item;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        commentRepository.deleteAll();

        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withResponseDefaults(prettyPrint())
                .withRequestDefaults(prettyPrint()))
                .build();

        user = userRepository.save(User.builder()
                .username("아이디")
                .password("비밀번호")
                .role(Role.USER)
                .build());

        item = itemRepository.save(Item.builder()
                .title("제목")
                .description("설명")
                .minPriceWanted(10_000)
                .imageUrl("사진")
                .user(user)
                .build());

        SecurityContext context1 = SecurityContextHolder.getContext();
        context1.setAuthentication(new UsernamePasswordAuthenticationToken(user.getId(), user.getPassword(), new ArrayList<>()));
    }

    @DisplayName("댓글 생성 API 테스트")
    @Test
    void createComment() throws Exception {
        // given

        CommentCreateRequestDto createDto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        String url = "http://localhost:8080/items/{itemId}/comments";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(post(url, item.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(new ObjectMapper().writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andDo(document("/comments",
                        requestFields(
                                fieldWithPath("content").description("내용")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("아이템 ID")
                        )
                ));

        // then

    }

    @DisplayName("댓글 수정 API 테스트")
    @Test
    void updateComment() throws Exception {
        // given
        Long commentId = createCommentMethod();

        CommentUpdateRequestDto updateDto = CommentUpdateRequestDto.builder()
                .content("수정내용")
                .build();

        String url = "http://localhost:8080/items/{itemId}/comments/{commentId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(put(url, item.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(document("/comments-update",
                        requestFields(
                                fieldWithPath("content").description("수정 내용")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("아이템 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )));

        // then

    }

    @DisplayName("댓글 삭제 API 테스트")
    @Test
    void deleteComment() throws Exception {
        // given
        Long commentId = createCommentMethod();

        String url = "http://localhost:8080/items/{itemId}/comments/{commentId}";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(delete(url, item.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication))
                .andExpect(status().isOk())
                .andDo(document("/comments-delete",
                        pathParameters(
                                parameterWithName("itemId").description("아이템 ID"),
                                parameterWithName("commentId").description("댓글 ID")
                        )));

        // then

    }

    @DisplayName("댓글 답글(reply) 수정 API")
    @Test
    void updateCommentReply() throws Exception {
        // given
        Long commentId = createCommentMethod();

        CommentReplyRequestDto replyDto = CommentReplyRequestDto.builder()
                .reply("답글내용")
                .build();

        String url = "http://localhost:8080/items/{itemId}/comments/{commentId}/reply";

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("" + user.getId());

        // when
        mvc.perform(put(url, item.getId(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(authentication)
                .content(new ObjectMapper().writeValueAsString(replyDto)))
                .andExpect(status().isOk())
                .andDo(document("/comments-reply",
                        requestFields(
                                fieldWithPath("reply").description("답글내용")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("아이템 글 번호"),
                                parameterWithName("commentId").description("댓글 번호")
                        )));

        // then

    }
    
    @DisplayName("댓글 페이징 조회 API")
    @Test
    void readAllComment() throws Exception {
        // given
        for (int i = 0; i < 6; i++) {
            CommentCreateRequestDto createDto = CommentCreateRequestDto.builder()
                    .content("내용" + i)
                    .build();
            
            commentService.create(item.getId(), createDto, user.getId());
        }

        String url = "http://localhost:8080/items/{itemId}/comments";

        // when
        mvc.perform(get(url, item.getId())
                .param("page", "0")
                .param("limit", "6")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("/comments-get-all",
                        pathParameters(
                                parameterWithName("itemId").description("아이템 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("현재 페이지"),
                                parameterWithName("limit").description("한 페이지당 조회할 데이터 개수")
                        )));


        // then
    }

    private Long createCommentMethod() {
        CommentCreateRequestDto dto = CommentCreateRequestDto.builder()
                .content("내용")
                .build();

        Comment comment = commentRepository.save(dto.toEntity(item, user));

        return comment.getId();
    }

    private Long createItem() {
        Item item = itemRepository.save(Item.builder()
                .title("제목")
                .description("내용")
                .minPriceWanted(10_000)
                .build());

        return item.getId();
    }
}