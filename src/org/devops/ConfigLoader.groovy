package org.devops

import org.yaml.snakeyaml.Yaml

class ConfigLoader implements Serializable {

    static def load(script, String filePath) {

        def yaml = new Yaml()

        def config = yaml.load(
            script.libraryResource(filePath)
        )

        return config
    }
}
