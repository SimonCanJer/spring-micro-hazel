package micro.examples.repository;

import micro.examples.data.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IToDoRepository extends JpaRepository<ToDo,Long> {
    @Query("FROM ToDo where addressee = ?1")
    List<ToDo> queryByAdressee(String addressedTo);
}
