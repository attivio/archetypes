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

#### New Version

If releasing a new version of the archetypes:

1. Checkout the `master` branch.
2. Merge `develop` into `master` as needed.
3. Configure the release version in the Maven poms:

   ```sh
   mvn versions:set -DremoveSnapshot
   mvn versions:set-property -Dproperty=attivio.version -DnewVersion=<version>
   ```

   where `<version>` is the four-field version of the Attivio SDK release with which this archetype release is associated.
4. Create a release branch for the new version:

   ```sh
   git branch release/<version>
   ```

   where `<version>` is the `Major.Minor` version of the Attivio SDK release with which this archetype release is associated.

5. Continue to the procedure [Performing the Release](#perform-the-release).

#### Existing Version

If releasing an update to an existing version of the archetypes:

1. Checkout the branch:

   ```sh
   git checkout <branch>
   ```

2. Increment the version:

   ```sh
   mvn versions:set -DnewVersion=<version>
   mvn versions:set-property -Dproperty=attivio.version -DnewVersion=<version>
   ```

   where `<version>` is the four-field version of the Attivio SDK release with which this archetype release is associated.

3. Continue to the procedure [Performing the Release](#perform-the-release).

### Perform the Release

1. Deploy the release artifacts to Bintray:

   ```sh
   mvn deploy [ -s </path/to/settings> ]
   ```

   where `[ -s </path/to/settings> ]` is an optional argument to Maven specifying a
   `settings.xml` containing the appropriate credentials for [Bintray](#bintray-setup).
2. Commit, tag, and push to GitHub:

   ```sh
   git commit -m "Archetype <version> release"
   git tag -a archetypes-<version>
   git push --tags
   ```

   where `<version>` is the four-field version of the Attivio SDK release with which this archetype release is associated.

### Post-Release

**Note**: Perform this procedure only for new versions.

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
