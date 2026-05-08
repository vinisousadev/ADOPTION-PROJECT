package br.com.adoption.service;

import br.com.adoption.dto.request.CreateFeedPostRequest;
import br.com.adoption.dto.request.CreateFeedPostCommentRequest;
import br.com.adoption.dto.request.PatchFeedPostCommentRequest;
import br.com.adoption.dto.request.CreateFeedPostVideoRequest;
import br.com.adoption.dto.request.PatchFeedPostRequest;
import br.com.adoption.dto.response.FeedPostCommentResponse;
import br.com.adoption.dto.response.FeedPostLikeResponse;
import br.com.adoption.dto.response.FeedPostResponse;
import br.com.adoption.dto.response.FeedPostVideoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FeedPostService {
    Page<FeedPostResponse> getAllPosts(Pageable pageable, String userEmail);
    FeedPostResponse save(CreateFeedPostRequest request, String userEmail);
    FeedPostResponse uploadPhoto(Long feedPostId, MultipartFile file, String userEmail);
    FeedPostResponse update(Long feedPostId, PatchFeedPostRequest request, String userEmail);
    FeedPostResponse delete(Long feedPostId, String userEmail);
    FeedPostLikeResponse like(Long feedPostId, String userEmail);
    FeedPostLikeResponse unlike(Long feedPostId, String userEmail);
    Page<FeedPostCommentResponse> getComments(Long feedPostId, Pageable pageable);
    FeedPostCommentResponse createComment(Long feedPostId, CreateFeedPostCommentRequest request, String userEmail);
    FeedPostCommentResponse updateComment(Long commentId, PatchFeedPostCommentRequest request, String userEmail);
    FeedPostCommentResponse deleteComment(Long commentId, String userEmail);
    FeedPostVideoResponse saveVideo(Long feedPostId, CreateFeedPostVideoRequest request, String userEmail);
    FeedPostVideoResponse deleteVideo(Long feedPostId, String userEmail);
}
