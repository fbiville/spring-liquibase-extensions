package com.github.lateralthoughts.liquibase;

import static java.lang.String.format;

import liquibase.exception.LiquibaseException;

class UnexpectedLiquibaseChangesetException extends LiquibaseException {

    public UnexpectedLiquibaseChangesetException(String messageTemplate,
                                                 Object... arguments) {

        super(format("\n--\n" + messageTemplate + "\n--", arguments));
    }
}
