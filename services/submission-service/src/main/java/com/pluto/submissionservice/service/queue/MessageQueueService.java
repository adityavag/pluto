package com.pluto.submissionservice.service.queue;

import java.util.Map;
import java.util.UUID;

public interface MessageQueueService {
    void queueEvaluation(UUID submissionId, String problemTitle, String problemDescription, Map<String, Object> excalidrawJson, String writeup);
}
