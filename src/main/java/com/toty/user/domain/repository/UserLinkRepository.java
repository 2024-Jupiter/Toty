package com.toty.user.domain.repository;

import java.util.List;

import com.toty.user.domain.model.UserLink;
import org.springframework.data.repository.Repository;

public interface UserLinkRepository extends Repository<UserLink, Long> {

    List<UserLink> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void save(UserLink userTag);
}