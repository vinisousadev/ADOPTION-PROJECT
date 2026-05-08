package br.com.adoption.service;

import br.com.adoption.entity.User;

public interface EmailDeliveryService {
    void sendEmailConfirmation(User user, String confirmationUrl);
}
