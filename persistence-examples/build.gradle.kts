dependencies {
    implementation(project(":persistence-core"))
    implementation(project(":persistence-mongodb"))
    implementation(project(":persistence-redis"))
    implementation(project(":persistence-nats"))
    implementation(project(":persistence-paper"))
    implementation(project(":persistence-velocity"))
    implementation(project(":persistence-bungee"))
    implementation(project(":persistence-sync"))
    implementation(project(":persistence-sector"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.21-R0.1-SNAPSHOT")
}
