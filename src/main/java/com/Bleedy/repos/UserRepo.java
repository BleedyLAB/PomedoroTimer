package com.Bleedy.repos;

import com.Bleedy.source.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<UserDB, Integer> {
    List<UserDB> findByUserID(Long userID);
    List<UserDB> findByUserName(String userName);
}
