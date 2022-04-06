def gitUrl = 'https://github.com/sonzqn/cdc-with-pact'

// Main build and deploy job for consumer and provider each (continuous deployment case)
['product-catalogue', 'product-service'].each {
    def app = it
    pipelineJob("$app-build-and-deploy") {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            url(gitUrl)
                        }
                        branch('master')
                        extensions {}
                    }
                }
                scriptPath("$app/jenkins/cd/Jenkinsfile")
            }
        }
    }
}

// Branch job for consumer
pipelineJob("product-catalogue-branch-with-removed-field") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(gitUrl)
                    }
                    branch('remove-field')
                    extensions {}
                }
            }
            scriptPath("product-catalogue/jenkins/cd/Jenkinsfile")
        }
    }
}

// Provider job that only executes contract tests, usually triggered by webhook
pipelineJob("product-service-run-contract-tests") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url(gitUrl)
                    }
                    branch('master')
                    extensions {}
                }
            }
            scriptPath("product-service/jenkins/Jenkinsfile-contract-tests")
        }
    }
}