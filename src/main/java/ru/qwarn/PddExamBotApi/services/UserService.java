package ru.qwarn.PddExamBotApi.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.qwarn.PddExamBotApi.models.User;
import ru.qwarn.PddExamBotApi.repositories.UserRepository;


import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    public User findByChatId(long chatId){
        Optional<User> user = userRepository.findByChatId(chatId);
        return user.isEmpty() ? null : user.get();
    }
}
