@NonCPS // cannot serialize regex.Matcher
def getVersionsFromTag(gittag) {
    def groups = (gittag =~ /(\d+)\.(\d+).(\d+)(?:(\S*)-(\d+))?/)
    if (!groups.matches()) {
//        return null;
        error("Could not determine (last) version from tag: " + git_tag)
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
                    currentBuild.result = 'SUCCESS'
                } catch (any) {
                    currentBuild.result = 'FAILURE'
                    throw any;
                }
            }
        }
    }
}

def nodeWithVersionHighTimeout(String label = '', version, cl) {
    node(label) {
        timeout(time:10, unit: 'HOURS') {
            withEnv(['BUILD_VERSION_SUFFIX=' + version]) {
                try {
                    cl()
                    currentBuild.result = 'SUCCESS'
                } catch (any) {
                    currentBuild.result = 'FAILURE'
                    throw any;
                }
            }
        }
    }
}

def withX(func) {
    wrap([$class: 'Xvnc', useXauthority: true]) {
        func()
    }
}

String[] runCmdAndSplit(command) {
    def stdout = sh (script: command, returnStdout: true).trim()
    return stdout.split("\n")
}

def withJUnit(cl){
    try {
        cl()
    } catch (Exception e) {
        throw e
    } finally {
        junit '**/test-results/test/*.xml,**/test-results/*.xml'
    }
}

def copyDownloads(src, dest) {
    sh "scp -i ~/.ssh/pushuser.key -P 18000 -r ${src} webpush@upload.actoron.com:${dest}"
}

def sshDownloads(cmd) {
    sh """ssh -i ~/.ssh/pushuser.key -p 18000 webpush@upload.actoron.com "${cmd}" """
}


return this