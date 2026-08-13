package com.backlogforge.web;

import com.backlogforge.application.usecase.GenerateBacklogUseCase;
import com.backlogforge.domain.ProductBacklog;
import com.backlogforge.web.dto.GenerateBacklogRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsável pelos endpoints de geração e gerenciamento de Product Backlog.
 */
@RestController
@RequestMapping("/api/v1/backlog")
@CrossOrigin(origins = "*")
public class BacklogController {

    private final GenerateBacklogUseCase generateBacklogUseCase;

    public BacklogController(GenerateBacklogUseCase generateBacklogUseCase) {
        this.generateBacklogUseCase = generateBacklogUseCase;
    }

    @PostMapping("/generate")
    public ResponseEntity<ProductBacklog> generateBacklog(
            @Valid @RequestBody GenerateBacklogRequest request
    ) {
        ProductBacklog backlog = generateBacklogUseCase.execute(request, null);
        return ResponseEntity.ok(backlog);
    }
}
