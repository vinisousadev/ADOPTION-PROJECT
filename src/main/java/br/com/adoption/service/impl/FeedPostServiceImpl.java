package br.com.adoption.service.impl;

import br.com.adoption.dto.request.CreateFeedPostRequest;
import br.com.adoption.dto.request.CreateFeedPostCommentRequest;
import br.com.adoption.dto.request.CreateFeedPostVideoRequest;
import br.com.adoption.dto.request.PatchFeedPostCommentRequest;
import br.com.adoption.dto.request.PatchFeedPostRequest;
import br.com.adoption.dto.response.FeedPostCommentResponse;
import br.com.adoption.dto.response.FeedPostLikeResponse;
import br.com.adoption.dto.response.FeedPostResponse;
import br.com.adoption.dto.response.FeedPostVideoResponse;
import br.com.adoption.entity.Animal;
import br.com.adoption.entity.AnimalStatus;
import br.com.adoption.entity.FeedPost;
import br.com.adoption.entity.FeedPostComment;
import br.com.adoption.entity.FeedPostLike;
import br.com.adoption.entity.FeedPostVideo;
import br.com.adoption.entity.User;
import br.com.adoption.entity.UserType;
import br.com.adoption.exception.InvalidFileUploadException;
import br.com.adoption.exception.OnlyOwnerCanManageUserException;
import br.com.adoption.exception.ResourceNotFoundException;
import br.com.adoption.mapper.FeedPostCommentMapper;
import br.com.adoption.mapper.FeedPostMapper;
import br.com.adoption.mapper.FeedPostVideoMapper;
import br.com.adoption.repository.AnimalRepository;
import br.com.adoption.repository.FeedPostCommentRepository;
import br.com.adoption.repository.FeedPostLikeRepository;
import br.com.adoption.repository.FeedPostRepository;
import br.com.adoption.repository.FeedPostVideoRepository;
import br.com.adoption.repository.UserRepository;
import br.com.adoption.service.FeedPostService;
import br.com.adoption.service.NotificationService;
import br.com.adoption.service.SupabaseStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class FeedPostServiceImpl implements FeedPostService {

    private static final long MAX_PHOTO_SIZE = 4 * 1024 * 1024;
    private static final Map<String, String> ALLOWED_PHOTO_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp"
    );

    private final FeedPostRepository feedPostRepository;
    private final FeedPostLikeRepository feedPostLikeRepository;
    private final FeedPostCommentRepository feedPostCommentRepository;
    private final FeedPostVideoRepository feedPostVideoRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SupabaseStorageService supabaseStorageService;

    public FeedPostServiceImpl(FeedPostRepository feedPostRepository,
                               FeedPostLikeRepository feedPostLikeRepository,
                               FeedPostCommentRepository feedPostCommentRepository,
                               FeedPostVideoRepository feedPostVideoRepository,
                               AnimalRepository animalRepository,
                               UserRepository userRepository,
                               NotificationService notificationService,
                               SupabaseStorageService supabaseStorageService) {
        this.feedPostRepository = feedPostRepository;
        this.feedPostLikeRepository = feedPostLikeRepository;
        this.feedPostCommentRepository = feedPostCommentRepository;
        this.feedPostVideoRepository = feedPostVideoRepository;
        this.animalRepository = animalRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.supabaseStorageService = supabaseStorageService;
    }

    @Override
    public Page<FeedPostResponse> getAllPosts(Pageable pageable, String userEmail) {
        User authenticatedUser = findUserByEmail(userEmail);
        return feedPostRepository.findAll(pageable)
                .map(feedPost -> toResponseWithEngagement(feedPost, authenticatedUser));
    }

    @Override
    public FeedPostResponse save(CreateFeedPostRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FeedPost feedPost = new FeedPost();
        feedPost.setAuthor(author);
        feedPost.setContent(request.getContent().trim());
        feedPost.setCreatedAt(OffsetDateTime.now());

        if (request.getAnimalId() != null) {
            Animal animal = animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Animal not found"));

            boolean isOwner = animal.getUser() != null
                    && animal.getUser().getId().equals(author.getId());

            if (!isOwner) {
                throw new OnlyOwnerCanManageUserException("Only the animal owner can link this animal to a feed post");
            }

            if (animal.getStatus() != AnimalStatus.AVAILABLE) {
                throw new IllegalArgumentException("Only available animals can be linked to a feed post");
            }

            feedPost.setAnimal(animal);
        }

        FeedPost savedPost = feedPostRepository.save(feedPost);
        return toResponseWithEngagement(savedPost, author);
    }

    @Override
    public FeedPostResponse uploadPhoto(Long feedPostId, MultipartFile file, String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        validateAuthorOrAdmin(feedPost, authenticatedUser);
        validatePhoto(file, "Feed post photo");

        String contentType = file.getContentType();
        String extension = ALLOWED_PHOTO_TYPES.get(contentType);

        try {
            String imageUrl = supabaseStorageService.uploadFeedPostPhoto(
                    feedPost.getId(),
                    file.getBytes(),
                    contentType,
                    extension
            );

            feedPost.setImageUrl(imageUrl);
            FeedPost updatedPost = feedPostRepository.save(feedPost);
            return toResponseWithEngagement(updatedPost, authenticatedUser);
        } catch (IOException exception) {
            throw new InvalidFileUploadException("Could not read feed post photo file");
        }
    }

    @Override
    public FeedPostResponse delete(Long feedPostId, String userEmail) {
        FeedPost feedPost = feedPostRepository.findById(feedPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed post not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateAuthorOrAdmin(feedPost, authenticatedUser);
        feedPostRepository.delete(feedPost);

        return toResponseWithEngagement(feedPost, authenticatedUser);
    }

    @Override
    public FeedPostResponse update(Long feedPostId, PatchFeedPostRequest request, String userEmail) {
        FeedPost feedPost = feedPostRepository.findById(feedPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed post not found"));

        User authenticatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateAuthorOrAdmin(feedPost, authenticatedUser);

        if (request.getContent() != null) {
            String trimmedContent = request.getContent().trim();

            if (trimmedContent.isEmpty()) {
                throw new IllegalArgumentException("Feed post content must not be blank");
            }

            feedPost.setContent(trimmedContent);
        }

        FeedPost updatedPost = feedPostRepository.save(feedPost);
        return toResponseWithEngagement(updatedPost, authenticatedUser);
    }

    @Override
    public FeedPostLikeResponse like(Long feedPostId, String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        boolean alreadyLiked = feedPostLikeRepository.existsByFeedPost_IdAndUser_Id(
                feedPost.getId(),
                authenticatedUser.getId()
        );

        if (!alreadyLiked) {
            FeedPostLike like = new FeedPostLike();
            like.setFeedPost(feedPost);
            like.setUser(authenticatedUser);
            like.setCreatedAt(OffsetDateTime.now());
            feedPostLikeRepository.save(like);

            notifyPostAuthor(
                    feedPost,
                    authenticatedUser,
                    "Nova curtida no seu post",
                    authenticatedUser.getName() + " curtiu sua publicacao.",
                    "FEED_POST_LIKED"
            );
        }

        return toLikeResponse(feedPost.getId(), authenticatedUser.getId(), true);
    }

    @Override
    public FeedPostLikeResponse unlike(Long feedPostId, String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        feedPostLikeRepository.findByFeedPost_IdAndUser_Id(feedPost.getId(), authenticatedUser.getId())
                .ifPresent(feedPostLikeRepository::delete);

        return toLikeResponse(feedPost.getId(), authenticatedUser.getId(), false);
    }

    @Override
    public Page<FeedPostCommentResponse> getComments(Long feedPostId, Pageable pageable) {
        findFeedPostById(feedPostId);
        return feedPostCommentRepository.findByFeedPost_IdOrderByCreatedAtDesc(feedPostId, pageable)
                .map(FeedPostCommentMapper::toResponse);
    }

    @Override
    public FeedPostCommentResponse createComment(Long feedPostId,
                                                 CreateFeedPostCommentRequest request,
                                                 String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        FeedPostComment comment = new FeedPostComment();
        comment.setFeedPost(feedPost);
        comment.setAuthor(authenticatedUser);
        comment.setContent(normalizeRequiredText(request.getContent(), "Feed post comment must not be blank"));
        comment.setCreatedAt(OffsetDateTime.now());

        FeedPostComment savedComment = feedPostCommentRepository.save(comment);
        notifyPostAuthor(
                feedPost,
                authenticatedUser,
                "Novo comentario no seu post",
                authenticatedUser.getName() + " comentou na sua publicacao.",
                "FEED_POST_COMMENTED"
        );
        return FeedPostCommentMapper.toResponse(savedComment);
    }

    @Override
    public FeedPostCommentResponse updateComment(Long commentId,
                                                 PatchFeedPostCommentRequest request,
                                                 String userEmail) {
        FeedPostComment comment = findCommentById(commentId);
        User authenticatedUser = findUserByEmail(userEmail);

        validateCommentAuthorOrAdmin(comment, authenticatedUser);

        if (request.getContent() != null) {
            comment.setContent(normalizeRequiredText(request.getContent(), "Feed post comment must not be blank"));
            comment.setUpdatedAt(OffsetDateTime.now());
        }

        FeedPostComment updatedComment = feedPostCommentRepository.save(comment);
        return FeedPostCommentMapper.toResponse(updatedComment);
    }

    @Override
    public FeedPostCommentResponse deleteComment(Long commentId, String userEmail) {
        FeedPostComment comment = findCommentById(commentId);
        User authenticatedUser = findUserByEmail(userEmail);

        validateCommentAuthorOrAdmin(comment, authenticatedUser);
        feedPostCommentRepository.delete(comment);

        return FeedPostCommentMapper.toResponse(comment);
    }

    @Override
    public FeedPostVideoResponse saveVideo(Long feedPostId, CreateFeedPostVideoRequest request, String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        validateAuthorOrAdmin(feedPost, authenticatedUser);

        FeedPostVideo video = feedPostVideoRepository.findByFeedPost_Id(feedPostId)
                .orElseGet(FeedPostVideo::new);

        video.setFeedPost(feedPost);
        video.setVideoUrl(normalizeRequiredText(request.getVideoUrl(), "Feed post video URL must not be blank"));
        video.setCreatedAt(video.getCreatedAt() != null ? video.getCreatedAt() : OffsetDateTime.now());

        FeedPostVideo savedVideo = feedPostVideoRepository.save(video);
        return FeedPostVideoMapper.toResponse(savedVideo);
    }

    @Override
    public FeedPostVideoResponse deleteVideo(Long feedPostId, String userEmail) {
        FeedPost feedPost = findFeedPostById(feedPostId);
        User authenticatedUser = findUserByEmail(userEmail);

        validateAuthorOrAdmin(feedPost, authenticatedUser);

        FeedPostVideo video = feedPostVideoRepository.findByFeedPost_Id(feedPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed post video not found"));

        feedPostVideoRepository.delete(video);
        return FeedPostVideoMapper.toResponse(video);
    }

    private void validateAuthorOrAdmin(FeedPost feedPost, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isAuthor = feedPost.getAuthor() != null
                && feedPost.getAuthor().getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new OnlyOwnerCanManageUserException("Only the post author or admin can manage this feed post");
        }
    }

    private void validateCommentAuthorOrAdmin(FeedPostComment comment, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getUserType() == UserType.ADMIN;
        boolean isAuthor = comment.getAuthor() != null
                && comment.getAuthor().getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isAuthor) {
            throw new OnlyOwnerCanManageUserException("Only the comment author or admin can manage this feed comment");
        }
    }

    private FeedPostResponse toResponseWithEngagement(FeedPost feedPost, User authenticatedUser) {
        long likeCount = feedPostLikeRepository.countByFeedPost_Id(feedPost.getId());
        long commentCount = feedPostCommentRepository.countByFeedPost_Id(feedPost.getId());
        boolean likedByCurrentUser = authenticatedUser != null
                && feedPostLikeRepository.existsByFeedPost_IdAndUser_Id(feedPost.getId(), authenticatedUser.getId());
        String videoUrl = feedPostVideoRepository.findByFeedPost_Id(feedPost.getId())
                .map(FeedPostVideo::getVideoUrl)
                .orElse(null);

        return FeedPostMapper.toResponse(feedPost, likeCount, commentCount, likedByCurrentUser, videoUrl);
    }

    private void notifyPostAuthor(FeedPost feedPost,
                                  User actor,
                                  String title,
                                  String message,
                                  String type) {
        User author = feedPost.getAuthor();

        if (author == null || actor == null || author.getId().equals(actor.getId())) {
            return;
        }

        notificationService.notify(
                author,
                title,
                message,
                type,
                "FEED_POST",
                feedPost.getId(),
                "/feed"
        );
    }

    private FeedPostLikeResponse toLikeResponse(Long feedPostId, Long userId, boolean liked) {
        FeedPostLikeResponse response = new FeedPostLikeResponse();
        response.setFeedPostId(feedPostId);
        response.setUserId(userId);
        response.setLiked(liked);
        response.setLikeCount(feedPostLikeRepository.countByFeedPost_Id(feedPostId));
        return response;
    }

    private FeedPost findFeedPostById(Long feedPostId) {
        return feedPostRepository.findById(feedPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed post not found"));
    }

    private FeedPostComment findCommentById(Long commentId) {
        return feedPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Feed post comment not found"));
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        String normalizedValue = value == null ? "" : value.trim();

        if (normalizedValue.isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }

        return normalizedValue;
    }

    private void validatePhoto(MultipartFile file, String label) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileUploadException(label + " is required");
        }

        if (file.getSize() > MAX_PHOTO_SIZE) {
            throw new InvalidFileUploadException(label + " must be at most 4MB");
        }

        if (!ALLOWED_PHOTO_TYPES.containsKey(file.getContentType())) {
            throw new InvalidFileUploadException(label + " must be JPG, PNG or WEBP");
        }
    }
}
