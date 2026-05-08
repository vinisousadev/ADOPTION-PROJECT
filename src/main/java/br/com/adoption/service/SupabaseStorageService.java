package br.com.adoption.service;

import br.com.adoption.exception.StorageUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Service
public class SupabaseStorageService {

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String profilePhotosBucket;
    private final String animalPhotosBucket;
    private final String feedPostPhotosBucket;

    public SupabaseStorageService(@Value("${supabase.url:}") String supabaseUrl,
                                  @Value("${supabase.service-role-key:}") String serviceRoleKey,
                                  @Value("${supabase.profile-photos-bucket:user-profile-photos}") String profilePhotosBucket,
                                  @Value("${supabase.animal-photos-bucket:animal-photos}") String animalPhotosBucket,
                                  @Value("${supabase.feed-post-photos-bucket:feed-post-photos}") String feedPostPhotosBucket) {
        this.restClient = RestClient.create();
        this.supabaseUrl = removeTrailingSlash(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.profilePhotosBucket = profilePhotosBucket;
        this.animalPhotosBucket = animalPhotosBucket;
        this.feedPostPhotosBucket = feedPostPhotosBucket;
    }

    public String uploadUserProfilePhoto(Long userId, byte[] fileBytes, String contentType, String extension) {
        String objectPath = "users/%d/profile-%d.%s".formatted(userId, System.currentTimeMillis(), extension);
        return uploadPublicObject(profilePhotosBucket, objectPath, fileBytes, contentType, "Could not upload profile photo");
    }

    public String uploadAnimalPhoto(Long animalId, byte[] fileBytes, String contentType, String extension) {
        String objectPath = "animals/%d/%s.%s".formatted(animalId, UUID.randomUUID(), extension);
        return uploadPublicObject(animalPhotosBucket, objectPath, fileBytes, contentType, "Could not upload animal photo");
    }

    public String uploadFeedPostPhoto(Long feedPostId, byte[] fileBytes, String contentType, String extension) {
        String objectPath = "feed-posts/%d/%s.%s".formatted(feedPostId, UUID.randomUUID(), extension);
        return uploadPublicObject(feedPostPhotosBucket, objectPath, fileBytes, contentType, "Could not upload feed post photo");
    }

    private String uploadPublicObject(String bucket, String objectPath, byte[] fileBytes, String contentType, String errorMessage) {
        validateStorageConfiguration();

        String uploadUrl = "%s/storage/v1/object/%s/%s".formatted(supabaseUrl, bucket, objectPath);

        try {
            restClient.put()
                    .uri(uploadUrl)
                    .header("apikey", serviceRoleKey)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + serviceRoleKey)
                    .header("x-upsert", "true")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(fileBytes)
                    .retrieve()
                    .toBodilessEntity();

            return "%s/storage/v1/object/public/%s/%s".formatted(supabaseUrl, bucket, objectPath);
        } catch (RestClientException exception) {
            throw new StorageUploadException(errorMessage, exception);
        }
    }

    private void validateStorageConfiguration() {
        if (supabaseUrl == null || supabaseUrl.isBlank() || serviceRoleKey == null || serviceRoleKey.isBlank()) {
            throw new StorageUploadException("Supabase Storage is not configured", null);
        }
    }

    private String removeTrailingSlash(String value) {
        if (value == null) {
            return "";
        }

        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
