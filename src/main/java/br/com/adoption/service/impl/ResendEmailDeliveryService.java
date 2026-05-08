package br.com.adoption.service.impl;

import br.com.adoption.entity.User;
import br.com.adoption.exception.EmailDeliveryException;
import br.com.adoption.service.EmailDeliveryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ResendEmailDeliveryService implements EmailDeliveryService {

    private final RestClient restClient;
    private final String apiKey;
    private final String fromEmail;
    private final boolean enabled;

    public ResendEmailDeliveryService(@Value("${resend.api-key:}") String apiKey,
                                      @Value("${resend.from-email:Adot <no-reply@adotapp.com>}") String fromEmail,
                                      @Value("${resend.enabled:false}") boolean enabled) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .build();
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.enabled = enabled;
    }

    @Override
    public void sendEmailConfirmation(User user, String confirmationUrl) {
        if (!enabled) {
            return;
        }

        if (isBlank(apiKey)) {
            throw new EmailDeliveryException("Email delivery is enabled but RESEND_API_KEY is missing");
        }

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:560px;margin:0 auto;color:#232820">
                  <h1 style="color:#4F9F6E">Confirme seu cadastro no Adot</h1>
                  <p>Oi, %s. Falta so confirmar seu email para liberar seu acesso.</p>
                  <p>
                    <a href="%s" style="display:inline-block;background:#4F9F6E;color:#fff;text-decoration:none;padding:12px 18px;border-radius:14px">
                      Confirmar email
                    </a>
                  </p>
                  <p>Se voce nao criou essa conta, ignore esta mensagem.</p>
                </div>
                """.formatted(user.getName(), confirmationUrl);

        String text = "Confirme seu cadastro no Adot: " + confirmationUrl;

        try {
            restClient.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", fromEmail,
                            "to", List.of(user.getEmail()),
                            "subject", "Confirme seu cadastro no Adot",
                            "html", html,
                            "text", text
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException exception) {
            throw new EmailDeliveryException("Could not send confirmation email");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
