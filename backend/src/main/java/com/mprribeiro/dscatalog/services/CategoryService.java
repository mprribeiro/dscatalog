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
import com.mprribeiro.dscatalog.entities.Category;
import com.mprribeiro.dscatalog.repositories.CategoryRepository;
import com.mprribeiro.dscatalog.services.exceptions.DatabaseException;
import com.mprribeiro.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAll(Pageable pageable) {
		var categories = categoryRepository.findAll(pageable);
		return categories.map(category -> new CategoryDTO(category));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		var category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found for id " + id));
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		var category = new Category();
		category.setName(dto.getName());
		category = categoryRepository.save(category);
		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			var category = categoryRepository.getById(id);
			category.setName(dto.getName());
			category = categoryRepository.save(category);
			return new CategoryDTO(category);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Entity not found for id " + id);
		}
	}
	
	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Entity not found for id " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}
}
