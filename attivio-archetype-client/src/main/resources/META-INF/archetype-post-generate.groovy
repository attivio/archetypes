def moduleDir = new File(request.getOutputDirectory()+"/"+request.getArtifactId())

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

if (attivioHome == null) {
    println "Client requires Attivio installation and Attivio was not found on path"
    attivioHome = System.console().readLine 'Attivio Installation Directory?: '
}
if (attivioHome != null) {
    println "Attivio installed at: ${attivioHome}"
}

def pomFile = new File(moduleDir, 'pom.xml')
println "Updating "+pomFile

def pomContent = pomFile.getText('UTF-8')

// update home directory
pomContent = pomContent.replace('ATTIVIO-HOME', attivioHome);

// rewrite pom.xml
pomFile.newWriter().withWriter { w ->
    w << pomContent
}
