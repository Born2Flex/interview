package ua.edu.internship.interview.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.internship.interview.data.enumeration.InterviewStatus;
import ua.edu.internship.interview.service.business.InterviewService;
import ua.edu.internship.interview.service.dto.interview.InterviewCreateDto;
import ua.edu.internship.interview.service.dto.interview.InterviewDto;
import ua.edu.internship.interview.service.dto.interview.InterviewUpdateDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionCreateDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionDto;
import ua.edu.internship.interview.service.dto.interview.question.InterviewQuestionUpdateDto;
import java.util.List;

@Tag(name = "Interviews", description = "Interviews management endpoints")
@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {
    private final InterviewService service;

    @GetMapping
    @Operation(summary = "Get all interviews", description = "Retrieves a list of all interviews.")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = InterviewDto.class))))
    public List<InterviewDto> getAllInterviews() {
        return service.getAllInterviews();
    }

    @GetMapping("/{interviewId}")
    @Operation(summary = "Get interview by ID", description = "Retrieves a specific interview by its ID.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = InterviewDto.class)))
    @ApiResponse(responseCode = "404", description = "Interview not found")
    public InterviewDto getInterviewById(@PathVariable String interviewId) {
        return service.getInterviewById(interviewId);
    }

    @PostMapping
    @Operation(summary = "Create a new interview", description = "Creates a new interview.")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = InterviewDto.class)))
    @ApiResponse(responseCode = "404", description = "Interviewer or candidate not found")
    @ApiResponse(responseCode = "400", description = "Interview causing conflict")
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewDto createInterview(@RequestBody @Valid InterviewCreateDto dto) {
        return service.createInterview(dto);
    }

    @PutMapping("/{interviewId}")
    @Operation(summary = "Update an existing interview", description = "Updates an existing interview.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = InterviewDto.class)))
    @ApiResponse(responseCode = "400", description = "Updated interview causing conflict")
    @ApiResponse(responseCode = "404", description = "Interview not found")
    public InterviewDto updateInterview(@PathVariable String interviewId, @RequestBody @Valid InterviewUpdateDto dto) {
        return service.updateInterview(interviewId, dto);
    }

    @DeleteMapping("/{interviewId}")
    @Operation(summary = "Delete an interview", description = "Deletes a specific interview by its ID.")
    @ApiResponse(responseCode = "204", description = "Interview deleted successfully")
    @ApiResponse(responseCode = "404", description = "Interview not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelInterview(@PathVariable String interviewId) {
        service.deleteInterviewById(interviewId);
    }

    @PatchMapping("/{interviewId}/status")
    @Operation(summary = "Update interview status", description = "Updates the status of an interview.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = InterviewDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Interview not found")
    public InterviewDto updateInterviewStatus(@PathVariable String interviewId, @RequestBody InterviewStatus status) {
        return service.updateInterviewStatus(interviewId, status);
    }

    @PatchMapping("/{interviewId}/feedback")
    @Operation(summary = "Update interview feedback", description = "Updates the feedback of an interview.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = InterviewDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Interview not found")
    public InterviewDto updateInterviewFeedback(@PathVariable String interviewId, @RequestBody String feedback) {
        return service.updateInterviewFeedback(interviewId, feedback);
    }

    @PostMapping("/{interviewId}/questions")
    @Operation(summary = "Create a new interview question", description = "Creates a new question for an interview.")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = InterviewQuestionDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ResponseStatus(HttpStatus.CREATED)
    public InterviewQuestionDto createInterviewQuestion(@PathVariable String interviewId,
                                                        @RequestBody @Valid InterviewQuestionCreateDto dto) {
        return service.createInterviewQuestion(interviewId, dto);
    }

    @PatchMapping("/{interviewId}/questions/{questionId}")
    @Operation(summary = "Update an existing interview question", description = "Updates an existing question for a specific interview.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = InterviewQuestionDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Question not found")
    public InterviewQuestionDto updateInterviewQuestion(@PathVariable String interviewId,
                                                        @PathVariable String questionId,
                                                        @RequestBody @Valid InterviewQuestionUpdateDto dto) {
        return service.updateInterviewQuestion(interviewId, questionId, dto);
    }

    @DeleteMapping("/{interviewId}/questions/{questionId}")
    @Operation(summary = "Delete an interview question", description = "Deletes a specific question for a specific interview.")
    @ApiResponse(responseCode = "204", description = "Question deleted successfully")
    @ApiResponse(responseCode = "404", description = "Question not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterviewQuestion(@PathVariable String interviewId, @PathVariable String questionId) {
        service.deleteInterviewQuestionById(interviewId, questionId);
    }
}
