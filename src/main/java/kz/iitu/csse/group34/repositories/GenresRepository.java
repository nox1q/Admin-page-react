package kz.iitu.csse.group34.repositories;
import kz.iitu.csse.group34.entities.Genres;
import kz.iitu.csse.group34.entities.Items;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenresRepository extends JpaRepository<Genres, Long> {

    List<Genres> findAll();
    Optional<Genres> findById(Long id);

}
