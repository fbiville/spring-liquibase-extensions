package com.github.lateralthoughts.liquibase;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.AssertJUnit.fail;

import java.util.ArrayList;
import java.util.List;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SpringLiquibaseCheckerTest {

    @Mock
    Liquibase liquibase;

    SpringLiquibaseChecker liquibaseChecker = new SpringLiquibaseChecker();

    @BeforeMethod
    public void prepare() {
        initMocks(this);
    }

    @Test(expectedExceptions = UnexpectedLiquibaseChangesetException.class)
    public void fails_if_one_unrun_changeset() throws LiquibaseException {
        given_a_dirty_changeset();

        liquibaseChecker.performUpdate(liquibase);
    }

    @Test
    public void does_not_fail_if_only_run_always_changesets() throws LiquibaseException {
        given_a_run_always_changeset();

        try {
            liquibaseChecker.performUpdate(liquibase);
        }
        catch(UnexpectedLiquibaseChangesetException exception) {
            fail("Run-always changesets should not trigger any exception.");
        }
    }

    @Test(expectedExceptions = UnexpectedLiquibaseChangesetException.class)
    public void fails_if_run_always_changeset_also_configured_to_run_on_change() throws LiquibaseException {
        given_a_dirty_run_on_change_and_run_always_changeset();

        liquibaseChecker.performUpdate(liquibase);
    }

    private void given_a_dirty_changeset() throws LiquibaseException {
        List<ChangeSet> changeSets = dirtyChangeSets();
        when(liquibase.listUnrunChangeSets(anyString())).thenReturn(changeSets);
    }

    private void given_a_run_always_changeset() throws LiquibaseException {
        List<ChangeSet> changeSets = runAlwaysChangeSets();
        when(liquibase.listUnrunChangeSets(anyString())).thenReturn(changeSets);
    }

    private void given_a_dirty_run_on_change_and_run_always_changeset() throws LiquibaseException {
        List<ChangeSet> changeSets = runAlwaysAndRunOnChangeChangeSets();
        when(liquibase.listUnrunChangeSets(anyString())).thenReturn(changeSets);
    }

    private List<ChangeSet> runAlwaysChangeSets() {
        List<ChangeSet> changeSets = new ArrayList<ChangeSet>();
        changeSets.add(runAlwaysChangeSet());
        return changeSets;
    }

    private List<ChangeSet> runAlwaysAndRunOnChangeChangeSets() {
        List<ChangeSet> changeSets = new ArrayList<ChangeSet>();
        changeSets.add(runAlwaysAndRunOnChangeChangeSet());
        return changeSets;
    }

    private List<ChangeSet> dirtyChangeSets() {
        List<ChangeSet> changeSets = new ArrayList<ChangeSet>();
        changeSets.add(anyChangeSet());
        return changeSets;
    }

    private ChangeSet runAlwaysChangeSet() {
        ChangeSet changeSet = anyChangeSet();
        when(changeSet.isAlwaysRun()).thenReturn(true);
        return changeSet;
    }

    private ChangeSet runAlwaysAndRunOnChangeChangeSet() {
        ChangeSet changeSet = anyChangeSet();
        when(changeSet.isAlwaysRun()).thenReturn(true);
        when(changeSet.isRunOnChange()).thenReturn(true);
        return changeSet;
    }

    private ChangeSet anyChangeSet() {
        ChangeSet changeset = mock(ChangeSet.class);
        when(changeset.getFilePath()).thenReturn("classpath:filepath");
        return changeset;
    }
}
