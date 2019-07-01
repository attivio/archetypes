# Testing and Releasing

## Testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

```sh
mvn clean install archetype:update-local-catalog
```

Then run a test by executing the following elsewhere:

```sh
mvn archetype:generate -DarchetypeCatalog=local
```

## Releasing

### Bintray Setup

1. Request membership to the [Attivio Bintray organization](https://bintray.com/attivio) from an Attivio contact.
2. Use the Bintray [Set Me Up](https://www.jfrog.com/confluence/display/BT/Main+Features#MainFeatures-SetMeUp) feature to generate the appropriate Maven server settings in your local Maven `settings.xml` config file.

### Prepare the Release

#### `develop` branch

If releasing a new version of the archetypes from
the `develop` branch:

1. Create a new branch for this release:

   ```sh
   git checkout -b <branch>
   ```

   where `<version>` is in the form `Major.Minor`

2. Drop the `SNAPSHOT` version identifiers:

   ```sh
   mvn versions:set -DremoveSnapshot
   mvn versions:set-property -Dproperty=attivio.version -DnewVersion=<version>
   ```

   where `<version>` is the four-field version of the Attivio SDK release with which this archetype release is associated.

3. Continue to the procedure [Performing the Release](#perform-the-release).

#### Existing branch

If releasing an update to archetypes from
an existing branch:

1. Checkout the branch:

   ```sh
   git checkout <branch>
   ```

2. Increment the version:

   ```sh
   mvn versions:set-property -Dproperty=attivio.version -DnewVersion=<version>
   ```

   where `<version>` is the four-field version of the Attivio SDK release with which this archetype release is associated.

3. Continue to the procedure [Performing the Release](#perform-the-release).

### Perform the Release

1. Commit, tag, and push to GitHub:

   ```sh
   git commit -m "<version> release"
   git tag -a archetypes-<version>
   git push --tags
   ```

### Post-Release

**Note**: Perform this procedure only if the release was done
from the `develop` branch.

1. Checkout the `develop` branch:

   ```sh
   git checkout develop
   ```

2. Increment the development version:

   ```sh
   mvn versions:set -DnewVersion=<next-version>
   mvn versions:set-property -Dproperty=attivio.version -DnewVersion=<next-version>
   ```

   where `<next-version>` is the next `SNAPSHOT` version.
