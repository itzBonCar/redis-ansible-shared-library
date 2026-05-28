def call() {

	def config = org.devops.ConfigLoader.load(
			this,
			"redis-config.yaml"
			)

		pipeline {

			agent any

				stages {

					stage('Clone') {

						steps {

							git branch: config.BRANCH,
							url: config.REPO_URL
						}
					}

					stage('User Approval') {

						when {
							expression {
								return config.KEEP_APPROVAL_STAGE == true
							}
						}

						steps {

							input message:
								"Approve Redis deployment to ${config.ENVIRONMENT} ?"
						}
					}


					stage('Playbook Execution') {

						steps {

							sshagent(credentials: ['super-ot-kp']) {

								sh """

									ansible-playbook -i ${config.INVENTORY} ${config.PLAYBOOK}

								"""
							}
						}
					}



					stage('Notification') {

						steps {

							slackSend(
									channel: "#${config.SLACK_CHANNEL_NAME}",
									message: """
									${config.ACTION_MESSAGE}

									Environment: ${config.ENVIRONMENT}

									Deployment Successful
									"""
)
						}
					}
				}

			post {

				failure {

					slackSend(
							channel: "#${config.SLACK_CHANNEL_NAME}",
							message: """
							Redis Deployment Failed
							"""
						 )
				}
			}
		}
}
