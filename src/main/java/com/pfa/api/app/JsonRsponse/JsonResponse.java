package com.pfa.api.app.JsonRsponse;

public class JsonResponse {
    
        private final int code;
        private final String message;

        public JsonResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    
}
