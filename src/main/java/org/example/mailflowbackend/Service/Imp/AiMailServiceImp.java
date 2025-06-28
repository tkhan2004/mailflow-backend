package org.example.mailflowbackend.Service.Imp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mailflowbackend.Dto.AiMailResponseDto;
import org.example.mailflowbackend.Service.AiMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiMailServiceImp implements AiMailService {


    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(AiMailServiceImp.class);

    @Override
    public AiMailResponseDto generateAiMail(String prompt) {
        // Tạo prompt yêu cầu Gemini trả về JSON thuần
        String systemPrompt = """
        Bạn là một chuyên gia viết mail chuyên nghiệp. 
        Vui lòng chỉ trả lời duy nhất dưới dạng JSON sau:
        {
          "receiverEmail": "abc@gmail.com",
          "subject": "Tiêu đề thư",
          "content": "Nội dung mail chi tiết"
        }
        KHÔNG viết gì ngoài JSON. Không chú thích. Không markdown.
        """;

        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", systemPrompt + "\n\n" + prompt))
        );

        Map<String, Object> body = Map.of(
                "contents", List.of(userMessage)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GEMINI_URL + geminiApiKey,
                    request,
                    Map.class
            );

            // Parse kết quả phản hồi
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("Gemini không trả về candidate nào.");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("Gemini không trả về nội dung nào.");
            }

            String raw = parts.get(0).get("text").toString();
            System.out.println("📦 Raw từ Gemini:\n" + raw);

// Cắt Markdown nếu có (```json ... ```)
            if (raw.startsWith("```")) {
                raw = raw.replaceAll("```json\\s*", "").replaceAll("```", "").trim();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(raw, AiMailResponseDto.class);

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Lỗi HTTP từ Gemini: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Lỗi HTTP từ Gemini: " + e.getStatusCode());
        } catch (JsonProcessingException e) {
            System.err.println("❌ Lỗi parse JSON từ Gemini:\n" + e.getMessage());
            throw new RuntimeException("Lỗi khi xử lý phản hồi từ Gemini: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Lỗi không xác định:\n" + e.getMessage());
            throw new RuntimeException("Lỗi không xác định khi gọi Gemini: " + e.getMessage());
        }
    }
}
