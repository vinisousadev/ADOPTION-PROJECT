package br.com.adoption.mapper;

import br.com.adoption.dto.request.CreateAdoptionRequest;
import br.com.adoption.dto.response.AdoptionRequestResponse;
import br.com.adoption.entity.AdoptionRequest;

import java.util.List;

public class AdoptionRequestMapper {

    public static AdoptionRequest toEntity(CreateAdoptionRequest request) {
        AdoptionRequest adoptionRequest = new AdoptionRequest();
        adoptionRequest.setMessage(request.getMessage());
        return adoptionRequest;
    }

    public static AdoptionRequestResponse toResponse(AdoptionRequest adoptionRequest) {
        AdoptionRequestResponse response = new AdoptionRequestResponse();
        response.setId(adoptionRequest.getId());
        response.setMessage(adoptionRequest.getMessage());
        response.setStatus(adoptionRequest.getStatus());
        response.setRequestDate(adoptionRequest.getRequestDate());
        response.setResponseDate(adoptionRequest.getResponseDate());

        if (adoptionRequest.getAnimal() != null) {
            response.setAnimalId(adoptionRequest.getAnimal().getId());
            response.setAnimalName(adoptionRequest.getAnimal().getAnimalName());

            if (adoptionRequest.getAnimal().getUser() != null) {
                response.setOwnerId(adoptionRequest.getAnimal().getUser().getId());
                response.setOwnerName(adoptionRequest.getAnimal().getUser().getName());
                response.setOwnerCity(adoptionRequest.getAnimal().getUser().getCity());
                response.setOwnerState(adoptionRequest.getAnimal().getUser().getState());
            }
        }

        if (adoptionRequest.getUser() != null) {
            response.setUserId(adoptionRequest.getUser().getId());
            response.setRequesterName(adoptionRequest.getUser().getName());
        }

        return response;
    }

    public static List<AdoptionRequestResponse> toResponseList(List<AdoptionRequest> requests) {
        return requests.stream()
                .map(AdoptionRequestMapper::toResponse)
                .toList();
    }
}
