package com.github.lateralthoughts.liquibase.filter;

import java.util.List;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.changelog.filter.ShouldRunChangeSetFilter;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;

/**
 * DirtyChangesetFilter - keeps only newly changed {@link liquibase.changelog.ChangeSet}.
 * This excludes, for instance, changesets marked as "runAlways" unless they also are
 * marked a "runOnChange" AND their checksum does not match the last one stored.
 *
 * @author Florent Biville (@fbiville)
 */
public class DirtyChangesetFilter extends ShouldRunChangeSetFilter {

    private final List<RanChangeSet> ranChangeSets;

    public DirtyChangesetFilter(Database database, boolean ignoreClasspathPrefix) throws DatabaseException {
        super(database, ignoreClasspathPrefix);
        this.ranChangeSets = database.getRanChangeSetList();
    }

    @Override
    public boolean accepts(ChangeSet changeSet) {
        for (RanChangeSet ranChangeSet : ranChangeSets) {
            if (changeSetsMatch(changeSet, ranChangeSet)) {
                if (changeSet.shouldRunOnChange() && checksumChanged(changeSet, ranChangeSet)) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }
}
