package banoun.aneece.repositories;



import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;


import banoun.aneece.model.Trader;

public interface TraderRepository extends CrudRepository<Trader, String>{
	
	Trader findByName(String name);
	Stream<Trader> findAllByName(String name);
	

}
