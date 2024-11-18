package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Skill;
import com.gr2.CVNest.service.SkillService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.NameAlreadyExistException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // CREATE SKILL
    @PostMapping("/skills")
    @ApiMessage("Create skill success")
    public ResponseEntity<Skill> createNewSkill(@Valid @RequestBody Skill reqSkill) throws NameAlreadyExistException {
        if (this.skillService.handleExistByName(reqSkill.getName())) {
            throw new NameAlreadyExistException("Skill name already exist");
        }
        Skill resSkill = this.skillService.handleCreateSkill(reqSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(resSkill);
    }

    // UPDATE SKILL
    @PutMapping("/skills")
    @ApiMessage("Update skill success")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill reqSkill) throws NameAlreadyExistException {
        Skill curSkill = this.skillService.handleGetSkillById(reqSkill.getId());
        if (curSkill == null) {
            throw new NameAlreadyExistException("Skill ID not found");
        }
        if (this.skillService.handleExistByName(reqSkill.getName())) {
            throw new NameAlreadyExistException("Skill name already exist");
        }
        curSkill.setName(reqSkill.getName());
        return ResponseEntity.ok().body(this.skillService.handleUpdateSkill(curSkill));
    }

    // GET ALL SKILLS
    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.skillService.handleGetAllSkill(spec, pageable));
    }

    // DELETE SKILL
    @DeleteMapping("/skills/{skillId}")
    @ApiMessage("Delete skill success")
    public ResponseEntity<Void> deleteSkill(@PathVariable("skillId") long skillId) throws NameAlreadyExistException {
        Skill reqSkill = this.skillService.handleGetSkillById(skillId);
        if (reqSkill == null) {
            throw new NameAlreadyExistException("Skill ID not found");
        }
        this.skillService.handleDeleteSkill(skillId);
        return ResponseEntity.ok().body(null);
    }
}
