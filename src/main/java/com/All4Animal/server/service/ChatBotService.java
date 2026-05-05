package com.All4Animal.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;


@Service
public class ChatBotService {

    private final RestClient restClient;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${custom.openai.vector-store-id}")
    private String vectorStoreId;

    public ChatBotService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    public String generateChatReply(String keyword) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5-mini",
                "instructions", """
                          너는 한국어 유기동물 입양 QA 챗봇이다. 너의 이름은 펫봇이야.

                          답변 원칙:
                          - 사용자의 질문에 먼저 직접 답한다.
                          - 입양 절차, 입양 지원금, 입양 후 책임, 동물등록, 중성화, 유실·유기동물 발견 대처는 file_search로 찾은 RAG 문서를 우선 근거로 답한다.
                          - 문서에 없는 내용은 지어내지 말고 보호센터, 관할 지자체, 정부24, 국가동물보호정보시스템 확인을 안내한다.
                          - 지역, 보호소, 동물 상태에 따라 달라질 수 있는 내용은 단정하지 않는다.
                          - 의료·법률 판단은 확정하지 말고 수의사나 공식 기관 확인을 안내한다.
                          - 답변은 따뜻하고 간결하게, 보통 2~5개 짧은 문단이나 bullet로 작성한다.
                          - 마지막에는 필요할 때 사용자의 상황을 묻는 꼬리 질문 1개를 덧붙인다.
                          """,
                "input", keyword,
                "tools", List.of(
                        Map.of(
                                "type", "file_search",
                                "vector_store_ids", List.of(vectorStoreId),
                                "max_num_results", 3
                        )
                ),
                "text", Map.of(
                        "verbosity", "medium"
                ),
                "reasoning", Map.of(
                        "effort", "low"
                ),
                "max_output_tokens", 1300
        );

        Map response = restClient.post()
                .uri("/responses")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return extractOutputText(response);
    }

    private String extractOutputText(Map response) {
        List<Map<String, Object>> output = (List<Map<String, Object>>) response.get("output");

        if (output == null) {
            return "답변을 생성하지 못했어요. 새로고침 후 다시 시도해주세요.";
        }

        for (Map<String, Object> item : output) {
            if (!"message".equals(item.get("type"))) {
                continue;
            }

            List<Map<String, Object>> content = (List<Map<String, Object>>) item.get("content");

            if (content == null) {
                continue;
            }

            for (Map<String, Object> contentItem : content) {
                if ("output_text".equals(contentItem.get("type"))) {
                    return (String) contentItem.get("text");
                }
            }
        }

        return "답변을 생성하지 못했어요.";
    }
}