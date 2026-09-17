package vn.feylix.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import vn.feylix.entity.Product;

public interface IProductService {
	List<Product> findAll();

	Optional<Product> findById(Long id);

	Optional<Product> findByProductName(String name);

	Optional<Product> findByCreateDate(Date createAt);

	<S extends Product> S save(S entity);

	void deleteById(Long id);

	void delete(Product entity);
}