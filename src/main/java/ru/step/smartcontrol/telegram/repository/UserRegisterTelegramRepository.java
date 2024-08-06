package ru.step.smartcontrol.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.step.smartcontrol.telegram.model.UserRegisterTelegram;

import java.util.Optional;

public interface UserRegisterTelegramRepository extends JpaRepository<UserRegisterTelegram, Long> {

    Optional<UserRegisterTelegram> findByTelegramChatId(Long id);
    Optional<UserRegisterTelegram> findByEmail(String email);

    @Query(value = "select urt.telegram_chat_id \n" +
            "          from user_register_telegram urt  \n" +
            "          where urt.email = (select us.e_mail from user_sc us where us.id = :userId ) \n" +
            "          and urt.is_active = true ;",
    nativeQuery = true)
    Long findChatIdByUserId(@Param("userId") Long userId);
}
