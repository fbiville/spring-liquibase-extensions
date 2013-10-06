# Spring Liquibase extensions

## Setup

### Downloads

```xml
<dependency>
    <groupId>com.lateral-thoughts</groupId>
    <artifactId>spring-liquibase-extensions</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Prerequisites

 * JDK 6+
 * Maven 2+
 * Spring 2.0.6+
 * Liquibase 3.1.0+ (see next section)

### Temporary setup instructions

 1. Clone [this forked version](https://github.com/fbiville/liquibase) of Liquibase
 1. `cd path/to/liquibase/fork; mvn clean install -DskipTests`
 1. Clone this repo and build it with `mvn clean install`

## "dirty" changeset check

### Motivation

[`SpringLiquibase`](http://www.liquibase.org/documentation/spring.html) is quite
convenient when it comes to the execution of migrations targeting a single standalone
application, at Spring context startup.

More often than not, though, multiple shared environments need to be kept in sync
and migrations are executed via Continuous Integration servers. That is when Maven,
Ant etc. come into play.

Following this approach, your shipped code might be slightly out of sync with
(actually: most likely ahead of) the latest database change sets executed by your CI.

### `SpringLiquibaseChecker` to the rescue!

`SpringLiquibaseChecker` sole purpose (for now) is to override SpringLiquibase
changeset execution behavior, replacing it with a dirty check.

If any changesets are to be run, a `LiquibaseException` is going to be thrown, thus
interrupting Spring context startup. If only changesets configured to
[run always](http://www.liquibase.org/documentation/changeset.html) are detected,
the check will pass.

### Example

See [this demo app](https://github.com/LateralThoughts/spring-liquibase-extensions-examples).
