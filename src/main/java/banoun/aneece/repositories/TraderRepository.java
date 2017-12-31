package banoun.aneece.repositories;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import banoun.aneece.model.Trader;

public interface TraderRepository extends CrudRepository<Trader, String>{
	Trader findByName(String name);
	List<Trader> findAllByName(String name);

}
