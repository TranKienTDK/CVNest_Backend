package com.gr2.CVNest.controller;

import com.gr2.CVNest.dto.response.ResCreateJobDTO;
import com.gr2.CVNest.dto.response.ResFetchJobDTO;
import com.gr2.CVNest.dto.response.ResUpdateJobDTO;
import com.gr2.CVNest.dto.response.ResultPaginationDTO;
import com.gr2.CVNest.entity.Job;
import com.gr2.CVNest.service.JobService;
import com.gr2.CVNest.util.annotation.ApiMessage;
import com.gr2.CVNest.util.error.EntityNotFoundException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class JobController {
    private final JobService jobService;

    // CREATE JOB
    @PostMapping("/jobs")
    @ApiMessage("Create job success")
    public ResponseEntity<ResCreateJobDTO> createNewJob(@Valid @RequestBody Job reqJob) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(reqJob));
    }

    // UPDATE JOB
    @PutMapping("/jobs")
    @ApiMessage("Update job success")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job reqJob) throws EntityNotFoundException {
        Job curJob = this.jobService.handleGetJobById(reqJob.getId());
        if (curJob == null) {
            throw new EntityNotFoundException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.handleUpdateJob(reqJob, curJob));
    }

    // GET ALL JOBS
    @GetMapping("/jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.handleGetAllJobs(spec, pageable));
    }

    // GET A JOB
    @GetMapping("/jobs/{jobId}")
    @ApiMessage("Get a job")
    public ResponseEntity<ResFetchJobDTO> getAJob(@PathVariable("jobId") long jobId) {
        Job reqJob = this.jobService.handleGetJobById(jobId);
        if (reqJob == null) {
            throw new EntityNotFoundException("Job not found");
        }
        return ResponseEntity.ok().body(this.jobService.convertJobToDTO(reqJob));
    }

    // DELETE JOB
    @DeleteMapping("/jobs/{jobId}")
    @ApiMessage("Delete job success")
    public ResponseEntity<Void> deleteJob(@PathVariable("jobId") long jobId) {
        Job reqJob = this.jobService.handleGetJobById(jobId);
        if (reqJob == null) {
            throw new EntityNotFoundException("Job not found");
        }
        this.jobService.handleDeleteJob(jobId);
        return ResponseEntity.ok().body(null);
    }
}
