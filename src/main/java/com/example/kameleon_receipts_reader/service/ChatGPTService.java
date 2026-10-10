package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.model.ChatResponse;
import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.model.Message;
import com.example.kameleon_receipts_reader.model.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This service analyses provided receipt text and provides detailed pricing info.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatGPTService implements AnalysisService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_FUNCTIONALITY_DESCRIPTION = """
            Parse the receipt and return **only the products/items that were purchased**.
            
            Ignore all non-product information, including store/company name and address, NIP, receipt number, tax information, payment information, card number, transaction numbers, cashier/register information, dates and times, marketing messages, BDO and system numbers, and VAT/tax summaries.
            
            For every purchased item, extract only:
            
            1. `name` — the product name exactly or approximately as it appears on the receipt.
            2. `quantity` — how many units were purchased.
            3. `unitPricePaid` — the effective price actually paid for one unit after discounts.
            
            ### Important rules
            
            * A product may occupy multiple OCR lines. Combine those lines into one product.
            * `OPUST` means a discount and must not be treated as a product.
            * Apply discounts to the corresponding product line when calculating `unitPricePaid`.
            * Preserve the quantity. Do not expand multiple units into separate products.
            * If a product has no discount, use its original unit price as `unitPricePaid`.
            * Calculate `unitPricePaid` as the final line price after discounts divided by the quantity.
            * Use decimal numbers, not Polish decimal commas.
            * If OCR contains obvious errors, correct them when the intended product name or number is reasonably clear.
            * Do not invent missing information. If a value genuinely cannot be determined, return `null`.
            * Do not include totals, taxes, payment amounts, or discount lines as products.
            
            ### Output format
            
            Return JSON only, using this structure:
            
            ```json
            {
              "items": [
                {
                  "name": "string",
                  "quantity": 2,
                  "unitPricePaid": 5.20
                }
              ]
            }
            ```
            
            """;
    /**
     * Key which specifies ChatGPT model.
     */
    public static final String MODEL_ATTRIBUTE = "model";
    /**
     * Model value.
     */
    public static final String GPT_VER_TO_USE = "gpt-5.6-luna";
    /**
     * List of configuration and user request messages to ChatGPT.
     */
    public static final String MESSAGES_ATTRIBUTE = "messages";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Your secret OpenAI API KEY.
     */
    @Value("${openai.api.key}")
    private String apiKey;

    /**
     * Which OpenAI REST API to call.
     */
    @Value("${openai.api.url}")
    private String apiURL;

    /**
     * Parses provided receipt text.
     * @param textToBeAnalysed text retrieved from receipt image.
     * @return pricing information.
     */
    public Items analyse(@NonNull String textToBeAnalysed) {
        ChatResponse response = restTemplate.exchange(apiURL, HttpMethod.POST, createHttpEntity(textToBeAnalysed), ChatResponse.class).getBody();
        if (!hasResponseMessage(response)) {
            throw new NoSuchElementException("No response returned from Chat Completions API");
        }
        return objectMapper.readValue(response.getChoices().getFirst().getMessage().getContent(), Items.class);
    }

    private HttpEntity<Map<String, Object>> createHttpEntity(String textToBeAnalysed) {
        return new HttpEntity<>(createRequestBody(textToBeAnalysed), createHeaders());
    }

    private static boolean hasResponseMessage(ChatResponse response) {
        return null != response && null != response.getChoices() && null != response.getChoices().getFirst()
                && null != response.getChoices().getFirst().getMessage();
    }

    private static Map<String, Object> createRequestBody(String textToBeAnalysed) {
        Message chatConfig = new Message(Role.SYSTEM.getRole(), CHAT_FUNCTIONALITY_DESCRIPTION);
        Message userRequest = new Message(Role.USER.getRole(), textToBeAnalysed);
        return Map.of(MODEL_ATTRIBUTE, GPT_VER_TO_USE,
                MESSAGES_ATTRIBUTE, List.of(chatConfig, userRequest));
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, BEARER + apiKey);
        headers.set(CONTENT_TYPE, APPLICATION_JSON);
        return headers;
    }

}
