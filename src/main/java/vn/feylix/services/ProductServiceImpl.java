package vn.feylix.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.feylix.entity.Product;
import vn.feylix.repository.ProductRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Override
	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	@Override
	public Optional<Product> findByProductName(String name) {
		return productRepository.findByProductName(name); // Định nghĩa ở repository[cite: 2]
	}

	@Override
	public Optional<Product> findByCreateDate(Date createAt) {
		return productRepository.findByCreateDate(createAt); // Định nghĩa ở repository[cite: 2]
	}

	@Override
	public <S extends Product> S save(S entity) {
		return productRepository.save(entity);
	}

	@Override
	public void deleteById(Long id) {
		productRepository.deleteById(id);
	}

	@Override
	public void delete(Product entity) {
		productRepository.delete(entity);
	}
}