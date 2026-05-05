package btl.kttkpm.quanlybanhangsieuthi.repository;

import btl.kttkpm.quanlybanhangsieuthi.entity.Item;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByNameContainingIgnoreCase(String name);

    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
