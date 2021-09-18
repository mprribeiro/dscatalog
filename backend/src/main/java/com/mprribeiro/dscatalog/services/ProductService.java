package com.mprribeiro.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mprribeiro.dscatalog.dto.CategoryDTO;
import com.mprribeiro.dscatalog.dto.ProductDTO;
import com.mprribeiro.dscatalog.entities.Category;
import com.mprribeiro.dscatalog.entities.Product;
import com.mprribeiro.dscatalog.repositories.CategoryRepository;
import com.mprribeiro.dscatalog.repositories.ProductRepository;
import com.mprribeiro.dscatalog.services.exceptions.DatabaseException;
import com.mprribeiro.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable) {
		var products = productRepository.findAll(pageable);	
		return products.map(product -> new ProductDTO(product));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found for id " + id));
		return new ProductDTO(product, product.getCategories());
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		var product = new Product();
		copyDTOToEntity(dto, product);
		product = productRepository.save(product);
		return new ProductDTO(product);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			var product = productRepository.getById(id);
			copyDTOToEntity(dto, product);
			product = productRepository.save(product);
			return new ProductDTO(product);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Entity not found for id " + id);
		}
	}
	
	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Entity not found for id " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
	
	private void copyDTOToEntity(ProductDTO dto, Product product) {
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setPrice(dto.getPrice());
		product.setImgUrl(dto.getImgUrl());
		product.setDate(dto.getDate());
		
		product.getCategories().clear();
		for (CategoryDTO categoryDTO : dto.getCategories()) {
			Category category = categoryRepository.getById(categoryDTO.getId());
			product.getCategories().add(category);
		}
		
	}
	
}
