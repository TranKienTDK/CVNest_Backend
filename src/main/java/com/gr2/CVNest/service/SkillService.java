package com.gr2.CVNest.service;

import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Skill;
import com.gr2.CVNest.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean handleExistByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleGetSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        return skillOptional.orElse(null);
    }

    public Skill handleCreateSkill(Skill reqSkill) {
        return this.skillRepository.save(reqSkill);
    }

    public Skill handleUpdateSkill(Skill reqSkill) {
        return this.skillRepository.save(reqSkill);
    }

    public ResultPaginationDTO handleGetAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

    public void handleDeleteSkill(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill curSkill = skillOptional.get();
        curSkill.getJobs().forEach(job -> job.getSkills().remove(curSkill));

        this.skillRepository.delete(curSkill);
    }
}
