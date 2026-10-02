package com.example.kameleon_receipts_reader.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Describes what a message sent to chatGPT means.
 * Will be used together with:
 * @see Message
 */
@RequiredArgsConstructor
public enum Role {

    /**
     * When this role is chosen, chatGPT will know that it is a question from a user.
     */
    USER("user"),
    /**
     * When this role is chosen, chatGPT will know that it is a content from a programmer of the chat's behaviour.
     */
    SYSTEM("system");

    @Getter
    private final String role;

}
