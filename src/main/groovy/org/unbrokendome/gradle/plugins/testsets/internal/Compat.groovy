package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.tasks.SourceSetOutput

class Compat {
    /**
     * Gets the classesDir (or dirs) of a {@link SourceSetOutput}.
     * Works in Gradle 3.x as well as 4.0 where the API changed - see
     * <a href="https://docs.gradle.org/4.0/release-notes.html#detecting-test-classes-for-custom-test-tasks">this link</a>.
     */
    static def classesDirsFor(SourceSetOutput output) {
        if (output.metaClass.respondsTo(output, "getClassesDirs")) {
            output.classesDirs
        } else {
            [output.classesDir]
        }
    }

    private Compat() {}
}
