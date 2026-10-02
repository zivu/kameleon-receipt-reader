package com.example.kameleon_receipts_reader.service;

import com.example.kameleon_receipts_reader.model.ChatResponse;
import com.example.kameleon_receipts_reader.model.Items;
import com.example.kameleon_receipts_reader.model.Message;
import com.example.kameleon_receipts_reader.model.Role;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class ChatGPTService implements AnalysisService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String BEARER = "Bearer ";
    public static final String CHAT_FUNCTIONALITY_DESCRIPTION = """
            Parse the receipt and return **only the products/items that were purchased**.
            
            Ignore all non-product information, including:
            
            * store/company name and address
            * NIP
            * receipt number
            * fiscal/tax information
            * payment information
            * card number
            * transaction numbers
            * cashier/register information
            * dates and times
            * marketing messages
            * BDO and system numbers
            * VAT/tax summaries
            
            For every purchased item, extract:
            
            1. `name` — the product name exactly or approximately as it appears on the receipt.
            2. `quantity` — how many units were purchased.
            3. `unitPrice` — the original price of one unit before any discount.
            4. `linePriceBeforeDiscount` — quantity × original unit price.
            5. `discount` — the total discount applied to this line. Use `0` if there is no discount.
            6. `linePrice` — the final amount actually paid for this line after discount.
            7. `unitPricePaid` — the effective price actually paid for one unit after discount.
            
            ### Price calculation
            
            Receipts commonly contain lines in this form:
            
            `quantity x unitPrice lineTotal`
            
            For example:
            
            `2 x6,50 13,00`
            
            means:
            
            * quantity = 2
            * original unit price = 6.50
            * line price before discount = 13.00
            
            If the following line is:
            
            `OPUST -2,60`
            
            then the discount for that product line is 2.60.
            
            Therefore:
            
            `linePrice = linePriceBeforeDiscount - discount`
            
            and:
            
            `unitPricePaid = linePrice / quantity`
            
            For example:
            
            `2 x6,50 13,00`
            `OPUST -2,60`
            `10,40B`
            
            should produce:
            
            * quantity: 2
            * unitPrice: 6.50
            * linePriceBeforeDiscount: 13.00
            * discount: 2.60
            * linePrice: 10.40
            * unitPricePaid: 5.20
            
            ### Important rules
            
            * A product may occupy multiple OCR lines. Combine those lines into one product.
            * `OPUST` means a discount and should **not** be treated as a product.
            * The amount immediately after the discount is normally the final price of that product line.
            * Do not create a separate product for a discount line.
            * Do not include `SUMA PLN`, `PTU`, `SPRZEDAŻ OPODATKOWANA`, or payment amounts as products.
            * Preserve the quantity. Do not expand `2 x6,50` into two separate products.
            * Use decimal numbers, not Polish decimal commas.
            * If OCR contains obvious errors, correct them when the intended product name or number is reasonably clear.
            * If the receipt contains a product without a discount, use its line total as `linePrice`.
            * Calculate `unitPricePaid` from the final line price divided by quantity rather than simply copying the original unit price.
            * Do not invent missing information. If a value genuinely cannot be determined, return `null`.
            
            Return JSON only, using this structure:
            
            {
            "items": [
            {
            "name": "string",
            "quantity": 0,
            "unitPrice": 0.00,
            "linePriceBeforeDiscount": 0.00,
            "discount": 0.00,
            "linePrice": 0.00,
            "unitPricePaid": 0.00
            }
            ]
            }
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
