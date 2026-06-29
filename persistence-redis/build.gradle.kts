dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))

    implementation("redis.clients:jedis:5.2.0")
}
