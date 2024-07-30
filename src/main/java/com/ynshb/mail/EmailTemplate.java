package com.ynshb.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {
    ACTIVATE_ACCOUNT("activate-account");

    private final String name;
}
