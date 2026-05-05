package com.All4Animal.server.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private final ChatClient chatClient;

    private static final String ANIMAL_TRAIT_SCHEMA = """
              {
                "type": "object",
                "additionalProperties": false,
                "required": ["traits"],
                "properties": {
                  "traits": {
                    "type": "object",
                    "additionalProperties": false,
                    "required": [
                      "people_friendly",
                      "active_playful",
                      "calm_quiet",
                      "adaptable",
                      "outdoor_activity",
                      "animal_friendly",
                      "beginner_possible",
                      "family_friendly",
                      "slow_bonding_ok"
                    ],
                    "properties": {
                      "people_friendly": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "active_playful": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "calm_quiet": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "adaptable": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "outdoor_activity": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "animal_friendly": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "beginner_possible": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "family_friendly": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] },
                      "slow_bonding_ok": { "type": "number", "enum": [0.0, 0.2, 0.5, 0.7, 0.9, 1.0] }
                    }
                  }
                }
              }
              """;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .maxCompletionTokens(2000)
                        .responseFormat(OpenAiChatModel.ResponseFormat.builder()
                                .type(OpenAiChatModel.ResponseFormat.Type.JSON_SCHEMA)
                                .jsonSchema(ANIMAL_TRAIT_SCHEMA)
                                .build()))
                .build();
    }

    public String scoreAnimalTraits(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
