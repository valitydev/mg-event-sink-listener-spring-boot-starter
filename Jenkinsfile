#!groovy
build('mg-event-sink-listener-spring-boot-starter', 'docker-host') {
    checkoutRepo()
    loadBuildUtils()

    def javaLibPipeline
    runStage('load JavaLib pipeline') {
        javaLibPipeline = load("build_utils/jenkins_lib/pipeJavaLibOutsideImage.groovy")
    }

    def buildImageTag = "fcf116dd775cc2e91bffb6a36835754e3f2d5321"
    javaLibPipeline(buildImageTag)
}
