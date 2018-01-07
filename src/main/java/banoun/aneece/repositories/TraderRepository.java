package banoun.aneece.repositories;
import java.util.List;


import org.springframework.data.mongodb.repository.MongoRepository;
import banoun.aneece.model.Trader;

public interface TraderRepository extends MongoRepository<Trader, String>{
	Trader findByName(String name);
	List<Trader> findAllByName(String name);

}
