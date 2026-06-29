dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))

    implementation("io.nats:jnats:2.20.6")
}
