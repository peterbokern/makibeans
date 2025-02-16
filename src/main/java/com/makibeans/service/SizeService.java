package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.Size;
import com.makibeans.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class SizeService extends AbstractCrudService<Size, Long> {

    private final SizeRepository sizeRepository;

    @Autowired
    public SizeService(JpaRepository<Size, Long> repository, SizeRepository sizeRepository) {
        super(repository);
        this.sizeRepository = sizeRepository;
    }

    public Size createSize(String name) {

        if (sizeRepository.existsByName(name)) {
            throw new DuplicateResourceException("Size with name " + name + " already exists.");
        }

        Size size = new Size(name);

        return sizeRepository.save(size);
    }

    public void deleteSize(Long sizeId){

    }
}
