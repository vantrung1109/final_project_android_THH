package com.example.projectfinaltth.data.model.request.document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    // Define fields based on your API response
    private boolean success;
    private String message;
    private DocumentData data;

    // Getters and Setters
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentData {
        // Define fields for document data
        private String id;
        private String title;
        private String description;
        // Getters and Setters
    }
}