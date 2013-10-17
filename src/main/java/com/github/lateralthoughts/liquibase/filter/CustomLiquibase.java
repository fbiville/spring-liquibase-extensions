package com.github.lateralthoughts.liquibase.filter;

import liquibase.Liquibase;
import liquibase.changelog.ChangeLogIterator;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.filter.ContextChangeSetFilter;
import liquibase.changelog.filter.DbmsChangeSetFilter;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ResourceAccessor;

/**
 * CustomLiquibase - overrides built-in ChangeLogIterator so that SpringLiquibaseChecker only detects newly modified
 * {@link liquibase.changelog.ChangeSet}s.
 * <p/>
 * This is particularly useful for {@link liquibase.changelog.ChangeSet}s that are both marked as "runAlways" and
 * "runOnChange". Because it is marked as "runAlways", default Liquibase implementation would consider it as a migration
 * to run, whereas {@link com.github.lateralthoughts.liquibase.SpringLiquibaseChecker} would filter them out.
 * <p/>
 * However, in {@link com.github.lateralthoughts.liquibase.SpringLiquibaseChecker} context, this kind of
 * migrations should be kept if and only when their contents have changed since last execution.
 *
 * @author Florent Biville
 */
public class CustomLiquibase extends Liquibase {

    public CustomLiquibase(String changeLogFile,
                           ResourceAccessor resourceAccessor,
                           Database database) throws LiquibaseException {

        super(changeLogFile, resourceAccessor, database);
    }

    protected ChangeLogIterator getStandardChangelogIterator(String contexts,
                                                             DatabaseChangeLog changeLog) throws DatabaseException {

        return new ChangeLogIterator(changeLog,
                new DirtyChangesetFilter(database, isIgnoreClasspathPrefix()),
                new ContextChangeSetFilter(contexts),
                new DbmsChangeSetFilter(database)
        );
    }
}
