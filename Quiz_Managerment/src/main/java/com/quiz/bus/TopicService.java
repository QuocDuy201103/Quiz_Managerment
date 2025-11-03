package com.quiz.bus;

import com.quiz.dao.TopicDAO;
import com.quiz.model.Topic;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for Topic-related operations.
 */
public class TopicService {
    private final TopicDAO topicDAO;

    public TopicService() {
        this.topicDAO = new TopicDAO();
    }

    public List<Topic> getAllTopics() {
        List<Topic> topics = topicDAO.getAllTopics();
        return topics != null ? topics : Collections.emptyList();
    }

    public List<Topic> getTopicsBySubject(int subjectId) {
        List<Topic> topics = topicDAO.getTopicsBySubject(subjectId);
        return topics != null ? topics : Collections.emptyList();
    }

    public boolean createTopic(Topic topic) {
        return topicDAO.addTopic(topic);
    }

    public boolean updateTopic(Topic topic) {
        return topicDAO.updateTopic(topic);
    }

    public boolean deleteTopic(int topicId) {
        return topicDAO.deleteTopic(topicId);
    }
}

