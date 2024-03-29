package ru.qwarn.pddexambotapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwarn.pddexambotapi.models.Selected;
import ru.qwarn.pddexambotapi.models.SelectedId;
import ru.qwarn.pddexambotapi.models.User;


import java.util.List;

@Repository
public interface SelectedRepository extends JpaRepository<Selected, SelectedId> {

    List<Selected> findAllByUserAndAlreadyWasFalse(User user);

    List<Selected> findAllByUser(User user);

}
