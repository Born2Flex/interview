package ua.edu.internship.interview.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.interview.service.business.SkillService;
import ua.edu.internship.interview.service.dto.skill.SkillTreeDto;
import ua.edu.internship.interview.web.handler.ErrorResponse;
import java.util.List;

@Tag(name = "Skills", description = "Skills management endpoints")
@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get all skills")
    @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(
            schema = @Schema(implementation = SkillTreeDto.class)), mediaType = "application/json")})
    public List<SkillTreeDto> getSkills() {
        return skillService.getAllSkillTrees();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get skills by root ID")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = SkillTreeDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public SkillTreeDto getSkillById(@PathVariable String id) {
        return skillService.getSkillTreeById(id);
    }
}
