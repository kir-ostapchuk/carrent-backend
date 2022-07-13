package com.ostapchuk.email.service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    private String holder;
    private String funds;

    public Message(
            @JsonProperty String holder,
            @JsonProperty String funds) {
        this.holder = holder;
        this.funds = funds;
    }

    public Message() {
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getFunds() {
        return funds;
    }

    public void setFunds(String funds) {
        this.funds = funds;
    }
}
