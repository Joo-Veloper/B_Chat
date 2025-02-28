package io.chat.domain.member.entity;

import io.chat.global.common.utils.TimeStamp;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private String pw;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
}
