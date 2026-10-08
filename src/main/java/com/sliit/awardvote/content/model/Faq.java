package com.sliit.awardvote.content.model;

import com.sliit.awardvote.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "faqs")
public class Faq extends BaseEntity {

    @Column(nullable = false)
    private String question;

    @Column(length = 2000, nullable = false)
    private String answer;

    private String category;
    private int displayOrder;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
