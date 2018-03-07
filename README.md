# Building and testing

To test, you'll want the archetype in a local catalog so that it can be found prior to publishing.  In the archetype directory:

    mvn clean install archetype:update-local-catalog

Then run a test by executing the following elsewhere:

    mvn archetype:generate -DarchetypeCatalog=local

# Releasing

## Maven Central Deployment Setup

Local setup procedure for deploying Attivio Maven artifacts to Maven Central.  This only needs to be done the first time.

* Log into the Sonatype Central Nexus Manager https://oss.sonatype.org using the same credentials as the [Sonatype JIRA](https://issues.sonatype.org)
* At the upper-right, select your username > _Profile_
* Select the Summary drop-down and choose _User Token_
* Select Access User Token > re-enter credentials
* Copy the the displayed `<server>...</server>` XML into a temporary location (the window will close after 1min)
* Open ~/.m2/settings.xml
* Add the copied server entry under the server element and replace ${server} with ossrh.
    <server>
      <id>ossrh</id>
      <username>(username string from token)</username>
      <password>(password string from token)</password>
    </server> 
* Save settings.xml and leave open.
* Open a terminal window.
* Obtain the Attivio Releng public GPG key.
    gpg --keyserver hkp://pool.sks-keyservers.net --recv-keys 8F7F7174
* Create a Maven master password.
```
mvn -emp
# > Password: (enter password)
# > Outputs {encrypted master password string}
```    
* Create a new file `~/.m2/settings-security.xml` with the following content. Be sure to include the opening and closing braces.
```
<settingsSecurity> 
  <master>(output of mvn -emp command above)</master> 
</settingsSecurity>
```
* Generate the encrypted GPG key passphrase for Maven.  _See Releng for the passphrase or access it from the [Releng password vault](https://git.attivio.com/attivio/relengdata/blob/master/team_releng.psafe3)_
```
mvn -ep
# > Password: (Attivio Releng GPG passphrase)
# > Outputs {encrypted passphrase string}
```
* Return to settings.xml and add another server entry under the ossrh element. Be sure to include the opening and closing braces.
```
<server>
  <id>8F7F7174</id>
  <passphrase>(output of mvn -ep command above)</passphrase>
</server>
```
* Add another server entry under the `8F7F7174` element.
```
<server>
  <id>github-local</id>
  <username>(public github.com username)</username>
  <password>(github.com personal oauth token)</password>
</server>
```
* Add a new profile entry under profiles.
```
<profile>
  <activation>
    <activeByDefault>true</activeByDefault>
  </activation>
  <properties>
    <gpg.keyname>8F7F7174</gpg.keyname>
  </properties>
</profile>
```
* Save and close settings.xml

## Doing the release

After updates or fixes are made to an Attivio archetype it needs to be published to Maven Central as a new released version.

First, test the release.  Run from your working copy directory and take the provided defaults when prompted for release version, SCM tag, and next version.

    $ mvn release:prepare -DdryRun=true

If the dry-run fails, review the recommendations given in the output and take action accordingly.

If the dry-run passes, review the simulated release run and ensure it matches expectations.  Example output snippet:

    [INFO] Full run would be commit 3 files with message: '[maven-release-plugin] prepare release archetypes-0.1.1'
    [INFO] Full run would be tagging working copy /home/mmasi/Temp/attivio-archetypes with label: 'archetypes-0.1.1'
    [INFO] Transforming 'platform/archetypes'...
    [INFO] Transforming 'platform/archetypes/attivio-archetype-module'...
    [INFO] Transforming 'platform/archetypes/attivio-archetype-client'...
    [INFO] Not removing release POMs
    [INFO] Executing completion goals - since this is simulation mode it is running against the original project, not the rewritten ones
    [INFO] Full run would be commit 3 files with message: '[maven-release-plugin] prepare for next development iteration'
    [INFO] Release preparation simulation complete.

When satisfied with the dry-run's proposals, the release can be deployed.  

    $ mvn release:clean release:prepare
    ...
    $ mvn release:perform -Prelease

Maven will have done the following:

* Set the archetypes' versions to release (by dropping SNAPSHOT)
* Tag the code in GitHub
* Deploy the artifacts to Maven Central
* Increment all poms to the next SNAPSHOT version
