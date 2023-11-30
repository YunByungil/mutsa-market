package com.example.market;

import com.example.market.api.controller.comment.CommentController;
import com.example.market.api.controller.item.ItemController;
import com.example.market.api.controller.negotiation.NegotiationController;
import com.example.market.api.controller.user.UserController;
import com.example.market.service.comment.CommentService;
import com.example.market.service.item.ItemService;
import com.example.market.service.negotiation.NegotiationService;
import com.example.market.service.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

//@WebMvcTest(controllers = {
//        UserController.class,
//        CommentController.class
//})
@WebMvcTest(controllers = {
        UserController.class,
        CommentController.class,
        ItemController.class,
        NegotiationController.class
}/*,
excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)*/)
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected UserService userService;

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected ItemService itemService;

    @MockBean
    protected NegotiationService negotiationService;

    @BeforeEach
    void setUp() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("1", "", List.of()));
    }
}
