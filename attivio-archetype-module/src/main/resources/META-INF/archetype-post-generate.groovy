// println binding.getVariables()
println "Cleaning up..."
def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId())
def confDir = new File(moduleDir, "src/main/resources/${artifactId}");
def webappsDir = new File(moduleDir, "src/main/resources/webapps/${artifactId}");

// replace com.sample with the group and artifact ids
moduleDir.eachDirRecurse() { dir ->
    dir.eachFileMatch(~/.*.java/) { file ->
        String code = file.getText('UTF-8').replaceAll('com.sample.module', request.getGroupId()+'.'+request.getArtifactId())
        file.newWriter().withWriter { w ->
            w << code
        }
    }
}

// Find Attivio installation
def attivioHome = System.getenv('ATTIVIO_HOME')
if (attivioHome == null) {
    systemPath = System.getenv('PATH')
    if (systemPath != null) {
        systemPath.split(System.getProperty("path.separator")).each { p ->
            if (new File(p+"/../conf/attivio.license").exists()) {
                attivioHome = new File(p).getParent();
            }
        }
    }
}

if (includeWeb && attivioHome == null) {
    println "Web inclusion requires Attivio installation and Attivio was not found on path"
    attivioHome = System.console().readLine 'Attivio Installation Directory?: '
}
if (attivioHome != null) {
    println "Attivio installed at: ${attivioHome}"
}

// handle inclusion of web servlet
def includeWebMarker = 'INCLUDE-WEB-MARKER'
def pomFile = new File(moduleDir, 'pom.xml')
println "Updating "+pomFile

def pomContent = pomFile.getText('UTF-8')
def webDependencies = """
    <!-- This is a non-SDK dependency.  Provides access to unsupported API.  Attivio installation required -->
    <dependency>
      <groupId>com.attivio.platform</groupId>
      <artifactId>app</artifactId>
      <version>\044{attivio.version}</version>
      <scope>system</scope>
      <systemPath>${attivioHome}/lib/aie-core-app.jar</systemPath>
    </dependency>
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
        <version>3.0.1</version>
    </dependency>
    <dependency>
       <groupId>org.apache.velocity</groupId>
       <artifactId>velocity</artifactId>
       <version>1.6.2</version>
    </dependency>
"""
if (includeWeb) {
    // replace marker with dependencies
    pomContent = pomContent.replace(includeWebMarker, webDependencies)
} else {
    // delete webapps directory and AdminServlet
    new File(moduleDir, 'src/main/resources/webapps').deleteDir()
    new File(moduleDir, 'src/main/java/'+request.getGroupId()+'/AdminServlet.java').delete()
    // remove marker from pom
    pomContent = pomContent.replace(includeWebMarker, '')
}

// rewrite pom.xml
pomFile.newWriter().withWriter { w ->
    w << pomContent
}
