plugins {
  java

  application
}

repositories {
  jcenter()
}

dependencies {
  implementation("com.rabbitmq:amqp-client:5.7.3")
  implementation("redis.clients:jedis:3.1.0")

  testImplementation("junit:junit:4.12")
}

application {
  mainClassName = "ru.bobko.shop.App"
}
