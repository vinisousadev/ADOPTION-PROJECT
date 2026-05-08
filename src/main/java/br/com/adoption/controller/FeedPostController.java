package br.com.adoption.controller;

import br.com.adoption.dto.request.CreateFeedPostRequest;
import br.com.adoption.dto.request.CreateFeedPostCommentRequest;
import br.com.adoption.dto.request.CreateFeedPostVideoRequest;
import br.com.adoption.dto.request.PatchFeedPostCommentRequest;
import br.com.adoption.dto.request.PatchFeedPostRequest;
import br.com.adoption.dto.response.FeedPostCommentResponse;
import br.com.adoption.dto.response.FeedPostLikeResponse;
import br.com.adoption.dto.response.FeedPostResponse;
import br.com.adoption.dto.response.FeedPostVideoResponse;
import br.com.adoption.service.FeedPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Feed Posts", description = "Feed post endpoints")
@RestController
@RequestMapping("/feed-posts")
public class FeedPostController {

    private final FeedPostService feedPostService;

    public FeedPostController(FeedPostService feedPostService) {
        this.feedPostService = feedPostService;
    }

    @GetMapping
    @Operation(summary = "List feed posts", description = "Returns paginated feed posts ordered by newest first")
    public PagedModel<FeedPostResponse> getAllPosts(
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return new PagedModel<>(feedPostService.getAllPosts(pageable, userEmail));
    }

    @PostMapping
    @Operation(summary = "Create feed post", description = "Creates a new feed post for the authenticated user")
    public FeedPostResponse createPost(@Valid @RequestBody CreateFeedPostRequest request,
                                       @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.save(request, userEmail);
    }

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload feed post photo", description = "Uploads a feed post photo through the backend. Allowed for the post author or admin")
    public FeedPostResponse uploadPostPhoto(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file,
                                            @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.uploadPhoto(id, file, userEmail);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update feed post", description = "Updates a feed post. Allowed for the post author or admin")
    public FeedPostResponse updatePost(@PathVariable Long id,
                                       @Valid @RequestBody PatchFeedPostRequest request,
                                       @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.update(id, request, userEmail);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete feed post", description = "Deletes a feed post. Allowed for the post author or admin")
    public FeedPostResponse deletePost(@PathVariable Long id,
                                       @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.delete(id, userEmail);
    }

    @PostMapping("/{id}/likes")
    @Operation(summary = "Like feed post", description = "Likes a feed post for the authenticated user")
    public FeedPostLikeResponse likePost(@PathVariable Long id,
                                         @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.like(id, userEmail);
    }

    @DeleteMapping("/{id}/likes")
    @Operation(summary = "Unlike feed post", description = "Removes the authenticated user's like from a feed post")
    public FeedPostLikeResponse unlikePost(@PathVariable Long id,
                                           @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.unlike(id, userEmail);
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "List feed post comments", description = "Returns paginated comments for a feed post")
    public PagedModel<FeedPostCommentResponse> getComments(
            @PathVariable Long id,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return new PagedModel<>(feedPostService.getComments(id, pageable));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Create feed post comment", description = "Creates a comment for a feed post")
    public FeedPostCommentResponse createComment(@PathVariable Long id,
                                                 @Valid @RequestBody CreateFeedPostCommentRequest request,
                                                 @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.createComment(id, request, userEmail);
    }

    @PatchMapping("/comments/{commentId}")
    @Operation(summary = "Update feed post comment", description = "Updates a comment. Allowed for the comment author or admin")
    public FeedPostCommentResponse updateComment(@PathVariable Long commentId,
                                                 @Valid @RequestBody PatchFeedPostCommentRequest request,
                                                 @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.updateComment(commentId, request, userEmail);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Delete feed post comment", description = "Deletes a comment. Allowed for the comment author or admin")
    public FeedPostCommentResponse deleteComment(@PathVariable Long commentId,
                                                 @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.deleteComment(commentId, userEmail);
    }

    @PostMapping("/{id}/video")
    @Operation(summary = "Add feed post video", description = "Adds or replaces a video URL for a feed post. Allowed for the post author or admin")
    public FeedPostVideoResponse saveVideo(@PathVariable Long id,
                                           @Valid @RequestBody CreateFeedPostVideoRequest request,
                                           @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.saveVideo(id, request, userEmail);
    }

    @DeleteMapping("/{id}/video")
    @Operation(summary = "Delete feed post video", description = "Deletes a video from a feed post. Allowed for the post author or admin")
    public FeedPostVideoResponse deleteVideo(@PathVariable Long id,
                                             @Parameter(hidden = true) Authentication authentication) {
        String userEmail = authentication != null ? authentication.getName() : null;
        return feedPostService.deleteVideo(id, userEmail);
    }
}
