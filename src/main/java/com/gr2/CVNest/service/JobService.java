package com.gr2.CVNest.service;

import com.gr2.CVNest.dto.response.ResCreateJobDTO;
import com.gr2.CVNest.dto.response.ResFetchJobDTO;
import com.gr2.CVNest.dto.response.ResUpdateJobDTO;
import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Job;
import com.gr2.CVNest.entity.Skill;
import com.gr2.CVNest.repository.JobRepository;
import com.gr2.CVNest.repository.SkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public Job handleGetJobById(Long id) {
        Optional<Job> jobOptional = this.jobRepository.findById(id);
        return jobOptional.orElse(null);
    }

    public ResFetchJobDTO convertJobToDTO(Job reqJob) {
        ResFetchJobDTO resFetchJobDTO = new ResFetchJobDTO();

        resFetchJobDTO.setId(reqJob.getId());
        resFetchJobDTO.setName(reqJob.getName());
        resFetchJobDTO.setDescription(reqJob.getDescription());
        resFetchJobDTO.setLevel(reqJob.getLevel());
        resFetchJobDTO.setLocation(reqJob.getLocation());
        resFetchJobDTO.setQuantity(reqJob.getQuantity());
        resFetchJobDTO.setStartSalary(reqJob.getStartSalary());
        resFetchJobDTO.setEndSalary(reqJob.getEndSalary());
        resFetchJobDTO.setStartDate(reqJob.getStartDate());
        resFetchJobDTO.setEndDate(reqJob.getEndDate());
        resFetchJobDTO.setActive(reqJob.isActive());
        resFetchJobDTO.setCreatedAt(reqJob.getCreatedAt());
        resFetchJobDTO.setUpdatedAt(reqJob.getUpdatedAt());

        if (reqJob.getSkills() != null) {
            List<String> skills = reqJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            resFetchJobDTO.setSkills(skills);
        }

        return resFetchJobDTO;
    }

    // CREATE JOB
    public ResCreateJobDTO handleCreateJob(Job reqJob) {
        if (reqJob.getSkills() != null) {
            List<Long> reqSkills = reqJob.getSkills()
                    .stream().map(x -> x.getId())
                    .toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            reqJob.setSkills(dbSkills);
        }

            Job curJob = this.jobRepository.save(reqJob);

            ResCreateJobDTO resJob = new ResCreateJobDTO();

            resJob.setId(curJob.getId());
            resJob.setName(curJob.getName());
            resJob.setLocation(curJob.getLocation());
            resJob.setDescription(curJob.getDescription());
            resJob.setQuantity(curJob.getQuantity());
            resJob.setStartSalary(curJob.getStartSalary());
            resJob.setEndSalary(curJob.getEndSalary());
            resJob.setStartDate(curJob.getStartDate());
            resJob.setEndDate(curJob.getEndDate());
            resJob.setActive(curJob.isActive());

            if (curJob.getSkills() != null) {
                List<String> skills = curJob.getSkills()
                        .stream().map(item -> item.getName())
                        .collect(Collectors.toList());
                resJob.setSkills(skills);
            }
        return resJob;
    }

    // GET ALL JOBS
    public ResultPaginationDTO handleGetAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageJob.getContent());

        return rs;
    }

    // UPDATE A JOB
    public ResUpdateJobDTO handleUpdateJob(Job reqJob) {
        if (reqJob.getSkills() != null) {
            List<Long> reqSkills = reqJob.getSkills()
                    .stream().map(x -> x.getId())
                    .toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            reqJob.setSkills(dbSkills);
        }

        Job curJob = this.jobRepository.save(reqJob);

        ResUpdateJobDTO resJob = new ResUpdateJobDTO();

        resJob.setId(curJob.getId());
        resJob.setName(curJob.getName());
        resJob.setLocation(curJob.getLocation());
        resJob.setDescription(curJob.getDescription());
        resJob.setQuantity(curJob.getQuantity());
        resJob.setStartSalary(curJob.getStartSalary());
        resJob.setEndSalary(curJob.getEndSalary());
        resJob.setStartDate(curJob.getStartDate());
        resJob.setEndDate(curJob.getEndDate());
        resJob.setActive(curJob.isActive());

        if (curJob.getSkills() != null) {
            List<String> skills = curJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            resJob.setSkills(skills);
        }
        return resJob;
    }

    // DELETE JOB
    public void handleDeleteJob(long id) {
        this.jobRepository.deleteById(id);
    }
}
