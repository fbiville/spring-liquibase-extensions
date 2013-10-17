package com.github.lateralthoughts.liquibase.filter;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.CheckSum;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DirtyChangesetFilterTest -
 *
 * @author Florent Biville
 */
public class DirtyChangesetFilterTest {

    private Database history;
    private List<RanChangeSet> ranChangesets;

    @BeforeMethod
    public void prepare() throws DatabaseException {
        history = mock(Database.class);
        ranChangesets = new ArrayList<RanChangeSet>();
    }

    @Test
    public void does_include_new_changesets() throws DatabaseException {
        given_one_ran_changeset_with_checksum("a", "ID");

        when(history.getRanChangeSetList()).thenReturn(ranChangesets);

        DirtyChangesetFilter filter = new DirtyChangesetFilter(history, true);
        assertThat(filter.accepts(runOnChangeAndAlwaysRunChangeSet("a", "ID2"))).isTrue();
    }

    @Test
    public void includes_newly_changed_existing_changesets() throws DatabaseException {
        given_one_ran_changeset_with_checksum("a", "ID");

        when(history.getRanChangeSetList()).thenReturn(ranChangesets);

        DirtyChangesetFilter filter = new DirtyChangesetFilter(history, true);
        assertThat(filter.accepts(runOnChangeChangeSet("b", "ID"))).isTrue();
    }

    @Test
    public void does_not_include_always_run_changesets() throws DatabaseException {
        given_one_ran_changeset_with_checksum("a", "ID");

        when(history.getRanChangeSetList()).thenReturn(ranChangesets);

        DirtyChangesetFilter filter = new DirtyChangesetFilter(history, true);
        assertThat(filter.accepts(alwaysRunChangeSet("a", "ID"))).isFalse();
    }

    @Test
    public void does_include_altered_run_on_change_changesets_even_when_marked_as_always_run() throws DatabaseException {
        given_one_ran_changeset_with_checksum("a", "ID");

        when(history.getRanChangeSetList()).thenReturn(ranChangesets);

        DirtyChangesetFilter filter = new DirtyChangesetFilter(history, true);
        assertThat(filter.accepts(runOnChangeAndAlwaysRunChangeSet("b", "ID"))).isTrue();
    }

    private void given_one_ran_changeset_with_checksum(String checksum, String id) {
        ranChangesets.add(ranChangeSet(checksum, id));
    }

    private RanChangeSet ranChangeSet(String checksum, String id) {
        RanChangeSet changeset = mock(RanChangeSet.class, RETURNS_DEEP_STUBS);
        when(changeset.getId()).thenReturn(id);
        when(changeset.getAuthor()).thenReturn("fbiville");
        when(changeset.getChangeLog()).thenReturn("/dev/null");
        when(changeset.getLastCheckSum()).thenReturn(CheckSum.parse(checksum));
        return changeset;
    }

    private ChangeSet runOnChangeAndAlwaysRunChangeSet(String checksum, String id) {
        ChangeSet changeSet = alwaysRunChangeSet(checksum, id);
        when(changeSet.isRunOnChange()).thenReturn(true);
        when(changeSet.shouldRunOnChange()).thenReturn(true);
        return changeSet;
    }

    private ChangeSet alwaysRunChangeSet(String checksum, String id) {
        ChangeSet changeSet = changeSet(checksum, id);
        when(changeSet.isAlwaysRun()).thenReturn(true);
        when(changeSet.shouldAlwaysRun()).thenReturn(true);
        return changeSet;
    }

    private ChangeSet runOnChangeChangeSet(String checksum, String id) {
        ChangeSet changeSet = changeSet(checksum, id);
        when(changeSet.isRunOnChange()).thenReturn(true);
        when(changeSet.shouldRunOnChange()).thenReturn(true);
        return changeSet;
    }

    private ChangeSet changeSet(String checksum, String id) {
        ChangeSet changeset = mock(ChangeSet.class, RETURNS_DEEP_STUBS);
        when(changeset.getId()).thenReturn(id);
        when(changeset.getAuthor()).thenReturn("fbiville");
        when(changeset.getFilePath()).thenReturn("/dev/null");
        when(changeset.generateCheckSum()).thenReturn(CheckSum.parse(checksum));
        return changeset;
    }
}
