package ru.qwarn.pddexambotapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.pddexambotapi.models.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {}
