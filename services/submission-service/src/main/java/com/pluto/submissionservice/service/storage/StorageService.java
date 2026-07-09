package com.pluto.submissionservice.service.storage;

import java.util.Map;

public interface StorageService {
    String storeJson(String fileName, Map<String, Object> json);
    Map<String, Object> retrieveJson(String pathOrUrl);
}
