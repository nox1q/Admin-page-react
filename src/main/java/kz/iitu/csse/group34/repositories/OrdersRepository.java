package kz.iitu.csse.group34.repositories;
import kz.iitu.csse.group34.entities.Books;
import kz.iitu.csse.group34.entities.Items;
import kz.iitu.csse.group34.entities.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    List<Orders> findAllByDeletedAtNull(Pageable pageable);
    List<Orders> findAllByUser_IdAndDeletedAtNull(Long user_id);
    Optional<Orders> findByIdAndDeletedAtNull(Long id);

}

