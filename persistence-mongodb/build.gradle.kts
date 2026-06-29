dependencies {
    api(project(":persistence-api"))
    implementation(project(":persistence-core"))

    api("dev.morphia.morphia:morphia-core:2.5.2")
    api("org.mongodb:mongodb-driver-sync:5.2.1")
}
