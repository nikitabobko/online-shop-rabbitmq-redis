plugins {
  java
}

repositories {
  jcenter()
}

dependencies {
  implementation("com.rabbitmq:amqp-client:5.7.3")
  implementation("redis.clients:jedis:3.1.0")
  implementation("com.google.code.gson:gson:2.8.6")

  testImplementation("junit:junit:4.12")
}

task("runBackend", type=JavaExec::class) {
  group = "Application"
  description = "Runs backend"
  classpath = sourceSets["main"].runtimeClasspath
  standardInput = System.`in`
//  standardOutput = System.out
  main = "ru.bobko.shop.backend.BackendMain"
}

task("runFrontend", type=JavaExec::class) {
  group = "Application"
  standardInput = System.`in`
//  standardOutput = System.out
  description = "Runs frontend"
  classpath = sourceSets["main"].runtimeClasspath
  main = "ru.bobko.shop.frontend.FrontendMain"
}
