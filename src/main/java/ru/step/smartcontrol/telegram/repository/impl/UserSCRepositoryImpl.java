package ru.step.smartcontrol.telegram.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.step.smartcontrol.telegram.dto.UserSC;
import ru.step.smartcontrol.telegram.exception.UserException;
import ru.step.smartcontrol.telegram.repository.UserSCRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.List;

@Slf4j
@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserSCRepositoryImpl implements UserSCRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public UserSC findByEmail(String email) {

        log.info("UserSCRepositoryImpl findByEmail email {}", email);

        String sql = "select us.id , us.first_name , us.last_name , us.middle_name , us.e_mail \n" +
                "from user_sc us \n" +
                "where us.e_mail = :email ";

        @SuppressWarnings("unchecked")
        List<Object[]> list = em.createNativeQuery(sql)
                .setParameter("email", email)
                .getResultList();

        if (list.isEmpty()) {
            throw new UserException("Такого email нет: " + email);
        }

        return getUserRC(list);
    }

    private static UserSC getUserRC(List<Object[]> list) {
        UserSC userSC = new UserSC();
        for (Object[] objects : list) {

            BigInteger id = (BigInteger) objects[0];
            String firstName = (String) objects[1];
            String lastName = (String) objects[2];
            String middleName = (String) objects[3];
            String eMail = (String) objects[4];

            if (id != null) {
                userSC.setId(id.longValue());
            }
            if (firstName != null) {
                userSC.setFirstName(firstName);
            }
            if (lastName != null) {
                userSC.setMiddleName(middleName);
            }
            if (eMail != null) {
                userSC.setEmail(eMail);
            }

        }
        return userSC;
    }
}
