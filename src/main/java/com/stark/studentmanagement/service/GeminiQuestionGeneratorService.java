package com.stark.studentmanagement.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stark.studentmanagement.entity.Exam;
import com.stark.studentmanagement.entity.Question;
import com.stark.studentmanagement.repository.ExamRepository;
import com.stark.studentmanagement.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiQuestionGeneratorService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final Gson gson = new Gson();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    public List<Question> generateAndSaveQuestions(Long examId, int count, String topicFocus, String difficultyLevel, String customApiKey) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("Exam not found with ID: " + examId));

        String subjectName = exam.getSubject() != null ? exam.getSubject().getName() : exam.getTitle();
        String effectiveKey = (customApiKey != null && !customApiKey.trim().isEmpty()) ? customApiKey.trim() : this.apiKey;
        List<Question> questions = new ArrayList<>();

        if (effectiveKey != null && !effectiveKey.trim().isEmpty()) {
            String prompt = buildPrompt(subjectName, topicFocus, count, difficultyLevel);
            String rawResponse = callGeminiApiWithFallback(prompt, effectiveKey);
            questions = parseQuestionsFromJson(rawResponse, exam);
        } else {
            // No API key specified anywhere -> generate mock questions as template
            questions = generateMockQuestions(exam, subjectName, topicFocus, count, difficultyLevel);
        }

        if (!questions.isEmpty()) {
            questionRepository.saveAll(questions);
        }

        return questions;
    }

    public List<Question> generateAndSaveQuestions(Long examId, int count, String topicFocus, String difficultyLevel) {
        return generateAndSaveQuestions(examId, count, topicFocus, difficultyLevel, null);
    }

    public List<Question> generateAndSaveQuestions(Long examId, int count, String topicFocus) {
        return generateAndSaveQuestions(examId, count, topicFocus, "MEDIUM", null);
    }

    private List<Question> generateMockQuestions(Exam exam, String subjectName, String topicFocus, int count, String difficultyLevel) {
        List<Question> list = new ArrayList<>();
        String topic = (topicFocus != null && !topicFocus.trim().isEmpty()) ? topicFocus : "Core Concepts";
        
        for (int i = 1; i <= count; i++) {
            Question q = new Question();
            q.setExam(exam);
            q.setQuestionText("[" + difficultyLevel + "] Q" + i + ": In " + subjectName + ", what is a fundamental property of " + topic + "?");
            q.setOptionA("Primary architectural specification and execution model for " + topic);
            q.setOptionB("Secondary runtime state buffer");
            q.setOptionC("Deprecated synchronous listener queue");
            q.setOptionD("None of the above");
            q.setCorrectAnswer("A");
            list.add(q);
        }
        return list;
    }

    private String buildPrompt(String subjectName, String topicFocus, int count, String difficultyLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert academic professor creating an examination paper for the subject '")
          .append(subjectName).append("'.\n");

        if (difficultyLevel != null && !difficultyLevel.trim().isEmpty()) {
            sb.append("Difficulty Level: ").append(difficultyLevel).append(".\n");
        }

        if (topicFocus != null && !topicFocus.trim().isEmpty()) {
            sb.append("Focus specifically on the topic: '").append(topicFocus).append("'.\n");
        }

        sb.append("Generate exactly ").append(count).append(" multiple-choice questions (MCQ).\n")
          .append("CRITICAL INSTRUCTION: Respond ONLY with a valid JSON array of objects without markdown formatting or code blocks.\n")
          .append("Each JSON object MUST have these exact fields:\n")
          .append("- \"questionText\": string\n")
          .append("- \"optionA\": string\n")
          .append("- \"optionB\": string\n")
          .append("- \"optionC\": string\n")
          .append("- \"optionD\": string\n")
          .append("- \"correctAnswer\": string (must be one of \"A\", \"B\", \"C\", or \"D\")\n\n")
          .append("Example format:\n")
          .append("[{\"questionText\":\"What is DFA?\",\"optionA\":\"Deterministic Finite Automaton\",\"optionB\":\"Dynamic File Array\",\"optionC\":\"Data Format Algorithm\",\"optionD\":\"Direct Frame Access\",\"correctAnswer\":\"A\"}]");

        return sb.toString();
    }

    private String callGeminiApiWithFallback(String prompt, String keyToUse) {
        String[] modelEndpoints = {
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent"
        };

        Exception lastException = null;

        for (String endpoint : modelEndpoints) {
            try {
                return callSingleGeminiEndpoint(endpoint, prompt, keyToUse);
            } catch (HttpClientErrorException e) {
                lastException = e;
                if (e.getStatusCode().value() == 400 || e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                    throw new RuntimeException("API Key or request rejected by Google (" + e.getStatusCode() + "): " + e.getResponseBodyAsString(), e);
                }
            } catch (Exception e) {
                lastException = e;
            }
        }

        if (lastException != null) {
            if (lastException instanceof RuntimeException) {
                throw (RuntimeException) lastException;
            }
            throw new RuntimeException("Gemini API call failed: " + lastException.getMessage(), lastException);
        }

        throw new RuntimeException("Gemini API call failed across all endpoints.");
    }

    private String callSingleGeminiEndpoint(String endpointUrl, String prompt, String keyToUse) {
        String fullUrl = endpointUrl + "?key=" + keyToUse;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray partsArray = new JsonArray();
        partsArray.add(textPart);

        JsonObject contentsObj = new JsonObject();
        contentsObj.add("parts", partsArray);

        JsonArray contentsArray = new JsonArray();
        contentsArray.add(contentsObj);

        JsonObject requestBody = new JsonObject();
        requestBody.add("contents", contentsArray);

        HttpEntity<String> entity = new HttpEntity<>(gson.toJson(requestBody), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(fullUrl, entity, String.class);
        return response.getBody();
    }

    private List<Question> parseQuestionsFromJson(String rawResponse, Exam exam) {
        List<Question> questions = new ArrayList<>();
        try {
            JsonObject jsonObject = gson.fromJson(rawResponse, JsonObject.class);
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");
            if (candidates == null || candidates.size() == 0) return questions;

            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");
            if (parts == null || parts.size() == 0) return questions;

            String textContent = parts.get(0).getAsJsonObject().get("text").getAsString().trim();
            
            // Clean markdown code blocks if Gemini returns ```json ... ```
            if (textContent.startsWith("```json")) {
                textContent = textContent.substring(7);
            } else if (textContent.startsWith("```")) {
                textContent = textContent.substring(3);
            }
            if (textContent.endsWith("```")) {
                textContent = textContent.substring(0, textContent.length() - 3);
            }
            textContent = textContent.trim();

            int firstBracket = textContent.indexOf('[');
            int lastBracket = textContent.lastIndexOf(']');
            if (firstBracket != -1 && lastBracket != -1 && lastBracket > firstBracket) {
                textContent = textContent.substring(firstBracket, lastBracket + 1);
            }

            JsonArray jsonArray = gson.fromJson(textContent, JsonArray.class);
            for (JsonElement element : jsonArray) {
                JsonObject qObj = element.getAsJsonObject();
                Question q = new Question();
                q.setExam(exam);
                q.setQuestionText(qObj.get("questionText").getAsString());
                q.setOptionA(qObj.get("optionA").getAsString());
                q.setOptionB(qObj.get("optionB").getAsString());
                q.setOptionC(qObj.get("optionC").getAsString());
                q.setOptionD(qObj.get("optionD").getAsString());
                q.setCorrectAnswer(qObj.get("correctAnswer").getAsString().toUpperCase());
                questions.add(q);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse questions generated by Gemini AI: " + e.getMessage(), e);
        }
        return questions;
    }
}
