package com.crud_base.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Transactional
    @Modifying
    @Query("""
            UPDATE UserEntity u SET 
            u.username =:username,
            u.surname =:surname,
            u.email =:email,
            u.age =:age
                    WHERE u.id =:id
            """)
    void updateUser(
            @Param("id") Long id,
            @Param("username") String username,
            @Param("surname") String surname,
            @Param("email") String email,
            @Param("age") Integer age
    );
}
