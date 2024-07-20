plugins {
	base
	java
	idea
	`maven-publish`
	alias(libs.plugins.architectury.loom)
	alias(libs.plugins.modpublisher)
	alias(libs.plugins.shadow)
}

val display = libs.versions.display

group = libs.versions.maven.group.get()
version = "${libs.versions.mod.get()}-${libs.versions.loader.get()}.${libs.versions.minecraft.get()}"

base {
	archivesName.set(libs.versions.archives.name)
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.shedaniel.me/") }
	maven { url = uri("https://maven.terraformersmc.com/releases/") }
}

dependencies {
	minecraft(libs.minecraft)
	mappings(libs.yarn) { artifact { classifier = "v2" } }
	forge(libs.forge)

	modApi(libs.cloth.config)

	api(libs.bundles.night.config)
	shadow(libs.bundles.night.config)

	// JUnit
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

	withSourcesJar()
}

tasks {
	processResources {
		filesMatching("fabric.mod.json") {
			expand(mapOf(
					"version" to libs.versions.mod.get(),
					"display" to display
			))
		}
	}

	shadowJar {
		relocate("com.electronwill.nightconfig", "${libs.versions.maven.group.get()}.shadow.nightconfig")
		archiveClassifier.set("dev-shadow")
	}

	remapJar {
		dependsOn(shadowJar)
	}

	jar {
		from("LICENSE")
	}

	test {
		useJUnitPlatform()
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = group.toString()
			artifactId = base.archivesName.get()

			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/KessokuTeaTime/${rootProject.name}")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}

publisher {
	apiKeys {
		modrinth(System.getenv("MODRINTH_TOKEN"))
		curseforge(System.getenv("CURSEFORGE_TOKEN"))
	}

	modrinthID.set(libs.versions.id.modrinth)
	curseID.set(libs.versions.id.curseforge)

	versionType.set("release")
	projectVersion.set(project.version.toString())
	gameVersions.set(listOf("1.20", "1.20.1"))
	loaders.set(listOf("forge"))
	curseEnvironment.set("both")

	modrinthDepends.required("cloth-config")
	modrinthDepends.optional()
	modrinthDepends.embedded()

	curseDepends.required("cloth-config")
	curseDepends.optional()
	curseDepends.embedded()

	displayName.set("${display.name.get()} ${libs.versions.mod.get()} for ${display.loader.get()} ${display.version.get()}")

	artifact.set(tasks.remapJar)
	addAdditionalFile(tasks.remapSourcesJar)

	changelog.set(file("CHANGELOG.md"))
}
