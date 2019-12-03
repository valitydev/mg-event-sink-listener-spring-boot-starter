#!groovy
build('mg-event-sink-listener-spring-boot-starter', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLib.groovy")
    }

    def buildImageTag = "a72d939ee965b89432cd5b502b3a369aad74093f"
    javaLibPipeline(buildImageTag)
}
