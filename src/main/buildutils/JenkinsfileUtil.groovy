@NonCPS // cannot serialize regex.Matcher
def getVersionsFromTag(gittag) {
    def groups = (gittag =~ /(\d+)\.(\d+).(\d+)(?:(\w*)-(\d+))?/)
    if (!groups.matches()) {
        throw new RuntimeException("Could not find version pattern in last git tag: " + gittag)
    }
    return [
            major: groups[0][1],
            minor: groups[0][2],
            patch: groups[0][3],
            branchName: groups[0][4],
            branchPatch: groups[0][5]
    ]
}

def nodeWithVersion(String label = '', version, cl) {
    node(label) {
        timeout(time:1, unit: 'HOURS') {
            withEnv(['BUILD_VERSION_SUFFIX=' + version]) {
                try {
                    cl()
                } catch (Exception e) {
                    println "Build Exception: ${e.getMessage()}"
                    throw e
                }
            }
        }
    }
}

return this