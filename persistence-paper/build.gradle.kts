dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))
    implementation(project(":persistence-plugin-messaging"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}
