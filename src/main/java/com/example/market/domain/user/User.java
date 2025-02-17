package com.example.market.domain.user;

import com.example.market.domain.comment.Comment;
import com.example.market.domain.item.Item;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String nickname;
    private String userImage;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "POINT SRID 4326")
    private Point location;

    private String address;

    @Enumerated(EnumType.STRING)
    private SearchScope searchScope = SearchScope.NORMAL;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public User(String username, String password, String phoneNumber, String email, String nickname,
                final String address, String userImage, Role role, final Point location) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.nickname = nickname;
        this.address = address;
        this.userImage = userImage;
        this.role = role;
        this.location = location;
    }

    public void updateCoordinate(final Point point) {
        this.location = point;
    }

    public void updateSearchScope(final SearchScope searchScope) {
        this.searchScope = searchScope;
    }
}
