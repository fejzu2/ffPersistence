dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))
    implementation(project(":persistence-plugin-messaging"))
    implementation(project(":persistence-sector"))

    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}
