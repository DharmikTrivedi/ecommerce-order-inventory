package com.oims.service;

import com.oims.exception.BusinessException;
import com.oims.exception.NotFoundException;
import com.oims.model.Seller;
import com.oims.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SellerService {

    private final SellerRepository sellerRepo;

    public SellerService(SellerRepository sellerRepo) {
        this.sellerRepo = sellerRepo;
    }

    public List<Seller> findAll() {
        return sellerRepo.findAllByOrderByNameAsc();
    }

    public List<Seller> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        return sellerRepo.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrderByName(keyword, keyword);
    }

    public Seller get(Integer id) {
        return sellerRepo.findById(id).orElseThrow(() -> new NotFoundException("Seller", id));
    }

    public boolean isEmailTaken(Seller s) {
        return s.getId() == null
                ? sellerRepo.existsByEmailIgnoreCase(s.getEmail())
                : sellerRepo.existsByEmailIgnoreCaseAndIdNot(s.getEmail(), s.getId());
    }

    @Transactional
    public Seller save(Seller s) {
        if (isEmailTaken(s)) {
            throw new BusinessException("Email " + s.getEmail() + " is already registered to another seller");
        }
        if (s.getGstin() != null && s.getGstin().isBlank()) {
            s.setGstin(null);
        }
        if (s.getId() == null) {
            s.setJoinedOn(LocalDate.now());
        } else {
            s.setJoinedOn(get(s.getId()).getJoinedOn());
        }
        return sellerRepo.save(s);
    }

    @Transactional
    public void delete(Integer id) {
        Seller s = get(id);
        if (!s.getProducts().isEmpty()) {
            throw new BusinessException("Seller '" + s.getName() + "' has products listed and cannot be deleted");
        }
        sellerRepo.delete(s);
    }
}
