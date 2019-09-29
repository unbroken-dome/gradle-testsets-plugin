package org.unbrokendome.gradle.plugins.testsets

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption


class DirectoryBuilder(private val path: Path) {

    init {
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
        check(Files.isDirectory(path)) { "Directory \"$path\" exists and is not a directory" }
    }

    fun directory(name: String, spec: DirectoryBuilder.() -> Unit) {
        val subPath = this.path.resolve(name)
        if (!Files.exists(subPath)) {
            Files.createDirectories(subPath)
        }
        check(Files.isDirectory(subPath)) { "Directory \"$subPath\" exists and is not a directory" }
        DirectoryBuilder(subPath).let(spec)
    }

    fun file(name: String, contents: String, append: Boolean = false, charset: Charset = Charsets.UTF_8) {
        val filePath = this.path.resolve(name)

        val options = arrayOf(
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            if (append) StandardOpenOption.APPEND else StandardOpenOption.TRUNCATE_EXISTING
        )

        Files.newBufferedWriter(filePath, charset, *options)
            .use { writer ->
                writer.append(contents.trimIndent())
            }
    }
}


fun directory(basePath: Path, spec: DirectoryBuilder.() -> Unit) =
    DirectoryBuilder(basePath).let(spec)


fun directory(basePath: File, spec: DirectoryBuilder.() -> Unit) =
    directory(basePath.toPath(), spec)


fun directory(basePath: String, spec: DirectoryBuilder.() -> Unit) =
    directory(Paths.get(basePath), spec)
