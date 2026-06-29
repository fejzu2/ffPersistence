dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))
    implementation(project(":persistence-plugin-messaging"))
    implementation(project(":persistence-sector"))

    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
}
